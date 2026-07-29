//
// Created by roman on 11/19/2024.
//

#include <dlfcn.h>
#include "CUtil.h"
#include "game/Textures/TextureDatabaseRuntime.h"


uintptr_t CUtil::FindLib(const char* libname)
{
    void* handle = dlopen(libname, RTLD_LAZY);
    if (handle) {
        void* symbol = dlsym(handle, "JNI_OnLoad");
        if (symbol) {
            Dl_info info;
            if (dladdr(symbol, &info) != 0) {
                return reinterpret_cast<uintptr_t>(info.dli_fbase);
            }
        }
        dlclose(handle);
    }

    return 0;
}

RwTexture* CUtil::LoadTextureFromDB(const char* dbname, const char* texture)
{
    TextureDatabaseRuntime* db_handle = TextureDatabaseRuntime::GetDatabase(dbname);
    if(!db_handle)
    {
        //FLog("Error: Database not found! (%s)", dbname);
        return nullptr;
    }

    TextureDatabaseRuntime::Register(db_handle);

    auto tex = CUtil::GetTexture(texture);
    if(!tex)
    {
        FLog("Error: Texture (%s) not found in database (%s)", dbname, texture);
        return nullptr;
    }

    TextureDatabaseRuntime::Unregister(db_handle);

    return tex;
}

#include <cmath>
float CUtil::GetDistanceBetween3DPoints(CVector* f, CVector* s)
{
    return sqrt(pow(s->x - f->x, 2) + pow(s->y - f->y, 2) + pow(s->z - f->z, 2));
}

RwTexture* CUtil::GetTexture(const char* name)
{
    auto tex = TextureDatabaseRuntime::GetTexture(name);
    if (!tex)
    {
        tex = CUtil::LoadTextureFromDB("gta3", "ahoodfence2");
        FLog("WARNING! No tex = %s", name);
        return tex;
    }
    ++tex->refCount;

    return tex;
}

void __fastcall CUtil::TransformPoint(RwV3d &result, const CSimpleTransform &t, const RwV3d &v)
{
    float cos_heading = cosf(t.m_fHeading);
    float sin_heading = sinf(t.m_fHeading);

    result = {
            t.m_vPosn.x + cos_heading * v.x - sin_heading * v.y,
            t.m_vPosn.y + sin_heading * v.x + cos_heading * v.y,
            v.z + t.m_vPosn.z
    };
}
#include <vector>
#include <iconv.h>
void cp1251_to_utf8(char* out, const char* in, unsigned int len) {
    static const unsigned short table[128] = {
            0xD082, 0xD083, static_cast<unsigned short>(0xE2809A), 0xD193, static_cast<unsigned short>(0xE2809E), static_cast<unsigned short>(0xE280A6), static_cast<unsigned short>(0xE280A0), static_cast<unsigned short>(0xE280A1),
            static_cast<unsigned short>(0xE282AC), static_cast<unsigned short>(0xE280B0), 0xD089, static_cast<unsigned short>(0xE280B9), 0xD08A, 0xD08C, 0xD08B, 0xD08F,
            0xD192, static_cast<unsigned short>(0xE28098), static_cast<unsigned short>(0xE28099), static_cast<unsigned short>(0xE2809C), static_cast<unsigned short>(0xE2809D), static_cast<unsigned short>(0xE280A2), static_cast<unsigned short>(0xE28093), static_cast<unsigned short>(0xE28094),
            0,       static_cast<unsigned short>(0xE284A2), 0xD199, static_cast<unsigned short>(0xE280BA), 0xD09A, 0xD09C, 0xD09B, 0xD09F,
            0xC2A0, 0xD08E, 0xD09E, 0xD088, 0xC2A4, 0xD290, 0xC2A6, 0xC2A7,
            0xD081, 0xC2A9, 0xD084, 0xC2AB, 0xC2AC, 0xC2AD, 0xC2AE, 0xD087,
            0xC2B0, 0xC2B1, 0xD086, 0xD196, 0xD291, 0xC2B5, 0xC2B6, 0xC2B7,
            0xD191, static_cast<unsigned short>(0xE29688), 0xD194, 0xC2BB, 0xD198, 0xD085, 0xD195, 0xD197,
            0xD090, 0xD091, 0xD092, 0xD093, 0xD094, 0xD095, 0xD096, 0xD097,
            0xD098, 0xD099, 0xD09A, 0xD09B, 0xD09C, 0xD09D, 0xD09E, 0xD09F,
            0xD0A0, 0xD0A1, 0xD0A2, 0xD0A3, 0xD0A4, 0xD0A5, 0xD0A6, 0xD0A7,
            0xD0A8, 0xD0A9, 0xD0AA, 0xD0AB, 0xD0AC, 0xD0AD, 0xD0AE, 0xD0AF,
            0xD0B0, 0xD0B1, 0xD0B2, 0xD0B3, 0xD0B4, 0xD0B5, 0xD0B6, 0xD0B7,
            0xD0B8, 0xD0B9, 0xD0BA, 0xD0BB, 0xD0BC, 0xD0BD, 0xD0BE, 0xD0BF,
            0xD180, 0xD181, 0xD182, 0xD183, 0xD184, 0xD185, 0xD186, 0xD187,
            0xD188, 0xD189, 0xD18A, 0xD18B, 0xD18C, 0xD18D, 0xD18E, 0xD18F
    };

    int count = 0;

    while (*in) {
        if (len && (count >= len - 1)) break;

        if (*in & 0x80) {
            unsigned short v = table[(unsigned char)(*in++) - 0x80];
            if (!v) continue;
            *out++ = (char)(v >> 8);
            *out++ = (char)(v & 0xFF);
        } else {
            *out++ = *in++;
        }

        count++;
    }

    *out = 0;
}