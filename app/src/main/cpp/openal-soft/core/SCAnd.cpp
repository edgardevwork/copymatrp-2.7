//
// SCAnd.cpp
// Compatibility implementation for the SCAnd symbols imported by libGTASA.so.
//
// This version follows the observable state/layout used by the supplied
// libSCAnd.so.lst.txt and libGTASA.so.lst.txt dumps.  It deliberately avoids
// fabricating GTASA C++ objects and keeps JNI/asset/cloud state self-contained.
//
/*
#include <android/log.h>
#include <android/asset_manager.h>
#include <dlfcn.h>
#include <jni.h>
#include <pthread.h>
#include <stdint.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>

#define SCAND_TAG "libSCAnd.so"

#ifndef SCAND_DEBUG
#define SCAND_DEBUG 0
#endif

#if SCAND_DEBUG
#define SCAND_LOG(...) __android_log_print(ANDROID_LOG_INFO, SCAND_TAG, __VA_ARGS__)
#else
#define SCAND_LOG(...) ((void)0)
#endif

// -----------------------------------------------------------------------------
// Exact globals observed in libSCAnd.so
// -----------------------------------------------------------------------------

// Dump: RockstarID is a .bss object of exactly 0x10 bytes.
extern "C" __attribute__((visibility("default"), used))
char RockstarID[0x10] = { 0 };

// Dump: LastUploadResult is a 32-bit .bss global.
extern "C" __attribute__((visibility("default"), used))
int LastUploadResult = 0;

// Dump: cloudMemCount is a 32-bit .bss global.
extern "C" __attribute__((visibility("default"), used))
int cloudMemCount = 0;

// Original SCAnd stores a pointer to a 0x410-byte cloud context.
struct CloudContext
{
    uint32_t state;                  // +0x000
    uint32_t lastError;              // +0x004
    char     filename[0x80];         // +0x008
    char     contentType[0x20];      // +0x088
    uint8_t  reserved[0x20];         // +0x0A8 starts buffer pointer below
    void*    buffer;                 // +0x0A8
    uint32_t bufferLen;              // +0x0B0
    uint8_t  reserved2[0x100];       // through +0x1B3
    uint8_t  mod0[0xC1];             // +0x1B4
    uint8_t  mod1[0xC1];             // +0x275
    uint8_t  mod2[0xC1];             // +0x336
    uint8_t  tail[0x410 - 0x3F7];    // remaining bytes
};

static CloudContext g_cloudContextStorage = {};
extern "C" __attribute__((visibility("default"), used))
CloudContext* cloudContext = &g_cloudContextStorage;

// -----------------------------------------------------------------------------
// Rockstar ID
// -----------------------------------------------------------------------------

static void SetRockstarIDInternal(const char* id)
{
    // Original authPopulateResponse uses strncpy(..., 0xF), then leaves a
    // terminating zero in the 0x10-byte global buffer.
    if (!id)
    {
        RockstarID[0] = '\0';
        return;
    }

    strncpy(RockstarID, id, 0xF);
    RockstarID[0xF] = '\0';
}

extern "C" __attribute__((visibility("default"), used))
const char* GetRockstarID()
{
    // Exact observable logic from the dump:
    // ADRP X0, RockstarID_ptr
    // LDR  X0, [X0, RockstarID_ptr]
    // RET
    return RockstarID;
}

// Optional setter for your future real-ID source/hook.
extern "C" __attribute__((visibility("default"), used))
void SetRockstarID(const char* id)
{
    SetRockstarIDInternal(id);
}

// -----------------------------------------------------------------------------
// JNI bridge
// -----------------------------------------------------------------------------

// Original SCAnd has an 8-byte JNIEnvFunc global. The actual function pointer
// returned by GTASA's AND_GetJNIFunc is NVThreadGetCurrentJNIEnv().
extern "C" __attribute__((visibility("default"), used))
void* (*JNIEnvFunc)(void) = nullptr;

// These two globals are also present in the original SCAnd and are used by
// its jniPreamble/getJNIEnv helpers.
extern "C" __attribute__((visibility("default"), used))
JNIEnv* g_jniEnv = nullptr;

extern "C" __attribute__((visibility("default"), used))
jobject g_jobject = nullptr;

// Exact mangled symbol:
// _Z13SetJNEEnvFuncPFPvvE
__attribute__((visibility("default"), used))
void* SetJNEEnvFunc(void* (*result)(void))
{
    // Exact original operation is simply:
    // JNIEnvFunc = result;
    JNIEnvFunc = result;
    return (void*)result;
}

// Exact mangled helper used by the original SCAnd JNI wrapper.
__attribute__((visibility("default"), used))
void* jniPreamble(JNIEnv* env, jobject obj)
{
    if (JNIEnvFunc)
        (void)JNIEnvFunc();

    g_jniEnv = env;

    if (!g_jobject)
        g_jobject = obj;

    return env;
}

__attribute__((visibility("default"), used))
void jniPostamble()
{
}

__attribute__((visibility("default"), used))
JNIEnv* getJNIEnv()
{
    if (JNIEnvFunc)
    {
        void* p = JNIEnvFunc();
        if (p)
            return reinterpret_cast<JNIEnv*>(p);
    }

    return g_jniEnv;
}

// These match the tiny helpers in the supplied SCAnd dump.
__attribute__((visibility("default"), used))
void* AND_GetJNI()
{
    return reinterpret_cast<void*>(getJNIEnv());
}

__attribute__((visibility("default"), used))
void* AND_GetObj()
{
    return g_jobject;
}

// -----------------------------------------------------------------------------
// Touch / Social Club entry points
// -----------------------------------------------------------------------------

extern "C" __attribute__((visibility("default"), used))
bool hasTouchScreen()
{
    // GTASA stores only the low byte of the return value.
    return true;
}

extern "C" __attribute__((visibility("default"), used))
void EnterGameFromSCFunc()
{
    // Callback placeholder. Do not create a fake GTASA object.
}

extern "C" __attribute__((visibility("default"), used))
void SigningOutfromApp()
{
}

// The original EnterSocialCLub constructs a real GTASA C++ object and calls
// hal::Screen::enterSocialClub(). Recreating that object from a replacement
// library is unsafe. GTASA's imported call does not consume a return value, so
// the compatibility version is deliberately a no-op.
__attribute__((visibility("default"), used))
long long EnterSocialCLub()
{
    return 0;
}

// -----------------------------------------------------------------------------
// Cloud compatibility
// -----------------------------------------------------------------------------

static inline CloudContext* Cloud()
{
    return cloudContext;
}

static inline uint8_t* CloudMod(CloudContext* c, int index)
{
    if (!c || index < 0 || index > 2)
        return nullptr;

    return c->mod0 + (size_t)index * 0xC1;
}

static int CloudFindIndex(CloudContext* c, const char* name)
{
    if (!c || !name)
        return -1;

    if (strcmp(reinterpret_cast<char*>(c->mod0 + 1), name) == 0)
        return 0;

    if (strcmp(reinterpret_cast<char*>(c->mod1 + 1), name) == 0)
        return 1;

    if (strcmp(reinterpret_cast<char*>(c->mod2 + 1), name) == 0)
        return 2;

    return -1;
}

extern "C" __attribute__((visibility("default"), used))
long long cloudStartDownload(char* name)
{
    CloudContext* c = Cloud();
    if (!c || !name)
        return 0;

    // Original:
    // if state != 0 -> 0
    // if buffer != NULL -> 0
    // strncpy(context+8, name, 0x7F)
    // error = 0
    // state = 1
    if (c->state != 0 || c->buffer != nullptr)
        return 0;

    strncpy(c->filename, name, sizeof(c->filename) - 1);
    c->filename[sizeof(c->filename) - 1] = '\0';
    c->lastError = 0;
    c->bufferLen = 0;

    // No network backend is installed, therefore do not claim that a busy
    // asynchronous request exists. The caller will see a safe empty result.
    c->state = 1;
    return 1;
}

extern "C" __attribute__((visibility("default"), used))
long long cloudStartCheckMod(char* name)
{
    CloudContext* c = Cloud();
    if (!c || !name)
        return 0;

    if (c->state != 0)
        return 0;

    if (c->buffer != nullptr)
        return 0;

    strncpy(c->filename, name, sizeof(c->filename) - 1);
    c->filename[sizeof(c->filename) - 1] = '\0';
    c->lastError = 0;
    c->state = 10;
    return 1;
}

extern "C" __attribute__((visibility("default"), used))
void* cloudStartUpload(
        const char* name,
        const char* contentType,
        const void* data,
        int size)
{
    CloudContext* c = Cloud();

    if (!c || !name || !contentType || size < 0)
        return nullptr;

    if (c->state != 0 || c->buffer != nullptr)
        return nullptr;

    strncpy(c->filename, name, sizeof(c->filename) - 1);
    c->filename[sizeof(c->filename) - 1] = '\0';

    strncpy(c->contentType, contentType, sizeof(c->contentType) - 1);
    c->contentType[sizeof(c->contentType) - 1] = '\0';

    if (size > 0)
    {
        if (!data)
            return nullptr;

        void* p = malloc((size_t)size);
        if (!p)
            return nullptr;

        memcpy(p, data, (size_t)size);
        c->buffer = p;
        c->bufferLen = (uint32_t)size;
        ++cloudMemCount;
    }
    else
    {
        c->buffer = nullptr;
        c->bufferLen = 0;
    }

    c->lastError = 0;
    c->state = 4;

    // Original function returns 1 on successful allocation/state transition.
    // It is declared void* in the ABI, so return a valid non-null status value
    // only for the successful call; the known GTASA caller ignores it.
    return reinterpret_cast<void*>(1);
}

extern "C" __attribute__((visibility("default"), used))
long long cloudModAddWatch(char* name)
{
    CloudContext* c = Cloud();
    if (!c || !name)
        return 0;

    int index = -1;

    // Exact selection logic from the dump:
    // if mod0 flag != 0 -> use 0
    // else if mod1 flag != 0 -> use 1
    // else if mod2 flag != 0 -> use 2
    // otherwise use 0.
    if (c->mod0[0] == 0)
        index = 0;
    else if (c->mod1[0] == 0)
        index = 1;
    else if (c->mod2[0] == 0)
        index = 2;
    else
        return 0;

    uint8_t* mod = CloudMod(c, index);
    if (!mod)
        return 0;

    // Original stores the name at slot+1 with max 0x7F bytes and clears
    // slot+0xB4 and slot+0x235 (relative to the whole context).
    mod[0] = 0;
    mod[0x81] = 0;
    strncpy(reinterpret_cast<char*>(mod + 1), name, 0x7F);
    mod[0x80] = '\0';

    return 1;
}

extern "C" __attribute__((visibility("default"), used))
void* cloudModFind(const char* name)
{
    CloudContext* c = Cloud();
    if (!c || !name)
        return nullptr;

    int index = CloudFindIndex(c, name);
    if (index < 0)
        return nullptr;

    uint8_t* mod = CloudMod(c, index);
    return mod ? reinterpret_cast<void*>(mod) : nullptr;
}

extern "C" __attribute__((visibility("default"), used))
long long cloudModReset(const char* name)
{
    CloudContext* c = Cloud();
    if (!c || !name)
        return 0;

    int index = CloudFindIndex(c, name);
    if (index < 0)
        return 0;

    uint8_t* mod = CloudMod(c, index);
    if (mod)
        mod[0] = 0;

    return 0;
}

extern "C" __attribute__((visibility("default"), used))
bool cloudIsBusy()
{
    // A real SC request is not running in this compatibility implementation.
    // Returning false prevents GTASA from waiting forever for a backend that
    // does not exist.
    return false;
}

extern "C" __attribute__((visibility("default"), used))
void* cloudGetBufferPtr()
{
    CloudContext* c = Cloud();
    return c ? c->buffer : nullptr;
}

extern "C" __attribute__((visibility("default"), used))
long long cloudGetBufferLen()
{
    CloudContext* c = Cloud();
    return c ? (long long)c->bufferLen : 0;
}

extern "C" __attribute__((visibility("default"), used))
void cloudGetFree()
{
    CloudContext* c = Cloud();

    if (!c || !c->buffer)
        return;

    free(c->buffer);
    c->buffer = nullptr;
    c->bufferLen = 0;

    if (cloudMemCount > 0)
        --cloudMemCount;
}

extern "C" __attribute__((visibility("default"), used))
long long GetCloudUploadResult()
{
    return LastUploadResult;
}

// -----------------------------------------------------------------------------
// Android AssetManager
// -----------------------------------------------------------------------------

static void* g_androidHandle = nullptr;
static pthread_once_t g_androidOnce = PTHREAD_ONCE_INIT;

static void InitAndroidHandle()
{
    g_androidHandle = dlopen("libandroid.so", RTLD_NOW | RTLD_LOCAL);
}

static void* AndroidHandle()
{
    pthread_once(&g_androidOnce, InitAndroidHandle);
    return g_androidHandle;
}

template <typename T>
static T AndroidSymbol(const char* name)
{
    void* h = AndroidHandle();
    if (!h || !name)
        return nullptr;

    return reinterpret_cast<T>(dlsym(h, name));
}

extern "C" __attribute__((visibility("default"), used))
void AAsset_close(AAsset* asset)
{
    using Fn = void (*)(AAsset*);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAsset_close");
    });

    if (fn)
        fn(asset);
}

extern "C" __attribute__((visibility("default"), used))
int AAsset_read(AAsset* asset, void* buf, size_t count)
{
    using Fn = int (*)(AAsset*, void*, size_t);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAsset_read");
    });

    return fn ? fn(asset, buf, count) : -1;
}

extern "C" __attribute__((visibility("default"), used))
off_t AAsset_seek(AAsset* asset, off_t offset, int whence)
{
    using Fn = off_t (*)(AAsset*, off_t, int);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAsset_seek");
    });

    return fn ? fn(asset, offset, whence) : (off_t)-1;
}

extern "C" __attribute__((visibility("default"), used))
off_t AAsset_getLength(AAsset* asset)
{
    using Fn = off_t (*)(AAsset*);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAsset_getLength");
    });

    return fn ? fn(asset) : 0;
}

extern "C" __attribute__((visibility("default"), used))
off64_t AAsset_getRemainingLength(AAsset* asset)
{
    using Fn = off64_t (*)(AAsset*);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAsset_getRemainingLength");
    });

    return fn ? fn(asset) : 0;
}

extern "C" __attribute__((visibility("default"), used))
AAsset* AAssetManager_open(
        AAssetManager* mgr,
        const char* filename,
        int mode)
{
    using Fn = AAsset* (*)(AAssetManager*, const char*, int);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAssetManager_open");
    });

    return fn ? fn(mgr, filename, mode) : nullptr;
}

extern "C" __attribute__((visibility("default"), used))
AAssetManager* AAssetManager_fromJava(
        JNIEnv* env,
        jobject assetManager)
{
    using Fn = AAssetManager* (*)(JNIEnv*, jobject);
    static Fn fn = nullptr;
    static pthread_once_t once = PTHREAD_ONCE_INIT;

    pthread_once(&once, []() {
        fn = AndroidSymbol<Fn>("AAssetManager_fromJava");
    });

    return fn ? fn(env, assetManager) : nullptr;
}

// -----------------------------------------------------------------------------
// Telemetry / profile compatibility
// -----------------------------------------------------------------------------

// Exact mangled symbol:
// _Z18TelemetryDataFlushv
__attribute__((visibility("default"), used))
long long TelemetryDataFlush()
{
    return 0;
}

// Exact mangled symbol:
// _Z17TelemetryDataSendPKcS0_
__attribute__((visibility("default"), used))
void TelemetryDataSend(const char* eventName, const char* data)
{
    // Original code builds std::string objects and forwards them into Social
    // Club. We intentionally do not construct SocialClubServices here because
    // that singleton belongs to the removed original SCAnd implementation.
    (void)eventName;
    (void)data;
}

// Exact mangled symbol:
// _Z16ProfileStatsSendPKci
__attribute__((visibility("default"), used))
long long ProfileStatsSend(const char* statName, int value)
{
    (void)statName;
    (void)value;
    return 0;
}

// Exact mangled symbol:
// _Z12IsSCSignedInv
__attribute__((visibility("default"), used))
long long IsSCSignedIn()
{
    // Returning false prevents GTASA's cloud update state machine from entering
    // SCCloudSaveStateUpdate when the real SocialClubServices singleton is not
    // present. This is safer than claiming a nonexistent authenticated session.
    return 0;
}

extern "C" __attribute__((visibility("default"), used))
bool IsProfileStatsBusy()
{
    return false;
}

// -----------------------------------------------------------------------------
// Per-frame compatibility entry point
// -----------------------------------------------------------------------------

extern "C" __attribute__((visibility("default"), used))
void scmainUpdate()
{
    // No Social Club backend is present. Intentionally no-op.
}

// -----------------------------------------------------------------------------
// Optional diagnostics
// -----------------------------------------------------------------------------

extern "C" __attribute__((visibility("default"), used))
void SCAndResetCompatibilityState()
{
    CloudContext* c = Cloud();

    if (c && c->buffer)
    {
        free(c->buffer);
        c->buffer = nullptr;
    }

    if (cloudMemCount > 0)
        cloudMemCount = 0;

    if (c)
        memset(c, 0, sizeof(*c));

    LastUploadResult = 0;
    RockstarID[0] = '\0';
}

// No ShadowHook dependency is hard-coded here. If you add ShadowHook later,
// hook only after libGTASA.so and the JVM are initialized; the dump shows that
// OS_GetDeviceInfo() relies on NVThreadGetCurrentJNIEnv() and a Java method ID
// named "GetDeviceInfo" with signature "(I)I".*/

#include <android/log.h>
