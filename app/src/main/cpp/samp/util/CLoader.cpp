//
// Created by Error on 25.04.2024.
//

#include "CLoader.h"
#include "main.h"
//#include "br/CDonate.h"
//#include "util/CSnapShotWrapper.h"
#include "clientrpc/CRpc.h"


void CLoader::LoadClass(JavaVM* vm){
    JNIEnv* env = nullptr;
    vm->GetEnv((void**)& env, JNI_VERSION_1_6);

    /*CSnapShotWrapper::clazz = env->FindClass("ru/edgar/space/GameRender");
    CSnapShotWrapper::clazz = (jclass) env->NewGlobalRef( CSnapShotWrapper::clazz );*/
    CRpc::clazzz = env->FindClass("ru/edgar/space/SAMP");
    CRpc::clazzz = (jclass) env->NewGlobalRef(CRpc::clazzz);
	
}