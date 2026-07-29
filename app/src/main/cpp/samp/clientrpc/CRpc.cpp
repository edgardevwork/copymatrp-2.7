//
// Created by EDGAR 3.0 on 11.06.2025.
//

// main
#include "main.h"

// .h
#include "CRpc.h"
#include "patch.h"

// UI
#include "java/jniutil.h"
#include "gui/gui.h"

// raknet
#include "raknet/BitStream.h"

// RPC
#include "clientrpc/anticheat/CAntiCheat.h"
#include "clientrpc/hud/CHUD.h"

// JSON Object
#include <vendor/nlohmann/json.hpp> // подключаем библиотеку

extern UI *pUI;
extern CNetGame *pNetGame;
extern CGame *pGame;
extern CJavaWrapper* g_pJavaWrapper;

jclass CRpc::clazzz;

bool CRpc::server = false;

CRpc::CRpc() {
/////Обьязательно принемать в i 16 _ t
}

using json = nlohmann::json; // псевдоним для удобства

extern "C" {

JNIEXPORT void JNICALL
Java_ru_edgar_space_SAMP_sendJsonData(JNIEnv *env, jobject thiz, jint guiId, jobject jsonObject) {
    // Получаем класс объекта JSONObject
    jclass cls = env->GetObjectClass(jsonObject);

    // Получаем метод toString()
    jmethodID toStringMethod = env->GetMethodID(cls, "toString", "()Ljava/lang/String;");

    // Вызываем метод toString() и получаем строку
    jstring strObj = static_cast<jstring>(env->CallObjectMethod(jsonObject, toStringMethod));

    // Преобразуем jstring в UTF-8 string
    const char* utf_chars = env->GetStringUTFChars(strObj, NULL);
    std::string jsonString(utf_chars);

    // Узнаем длину строки
    size_t jsonSize = jsonString.size();

    // Не забываем освободить ресурс!
    env->ReleaseStringUTFChars(strObj, utf_chars);
    env->DeleteLocalRef(strObj);

    FLog("RPC send client %d", guiId);
    // Далее формируем BitStream для отправки
    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)ID_CUSTOM_RPC);      // Магическое число
    bsSend.Write((uint8_t)guiId);              // ID GUI
    bsSend.Write((uint16_t)jsonSize);          // Длина JSON
    bsSend.Write(jsonString.c_str(), jsonSize); // Данные JSON

    FLog("RPC JSON: %s", jsonString.c_str());

    // И отправляем пакет
    pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);
}
JNIEXPORT void JNICALL Java_ru_edgar_space_SAMP_sendAlt(JNIEnv *env, jobject thiz) {
    //LocalPlayerKeys.bKeys[ePadKeys::KEY_WALK] = true;
    /*if(CFirstPersonCamera::IsEnabled()) {
        CFirstPersonCamera::SetEnabled(false);
    } else {
        CFirstPersonCamera::SetEnabled(true);
    }*/
}
}

void CRpc::sendJsonData(jint guiId, jstring jsonObject) {
    JNIEnv *env = g_pJavaWrapper->GetEnv();

    // Вызываем метод toString() и получаем строку
    jstring strObj = jsonObject;

    // Преобразуем jstring в UTF-8 string
    const char* utf_chars = env->GetStringUTFChars(strObj, NULL);
    std::string jsonString(utf_chars);

    // Узнаем длину строки
    size_t jsonSize = jsonString.size();

    // Не забываем освободить ресурс!
    env->ReleaseStringUTFChars(strObj, utf_chars);
    env->DeleteLocalRef(strObj);

    FLog("RPC send client %d", guiId);
    // Далее формируем BitStream для отправки
    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)ID_CUSTOM_RPC);      // Магическое число
    bsSend.Write((uint8_t)guiId);              // ID GUI
    bsSend.Write((uint16_t)jsonSize);          // Длина JSON
    bsSend.Write(jsonString.c_str(), jsonSize); // Данные JSON

    // И отправляем пакет
    pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);
}

bool CRpc::propv() {
    if(!pNetGame) return false;
    if(pNetGame->GetGameState() != GAMESTATE_CONNECTED) return false;
    CPlayerPed *pPlayerPed = pGame->FindPlayerPed();
    if(!pPlayerPed->IsInVehicle() && !pPlayerPed->IsAPassenger()) {
        CVehiclePool *pVehiclePool = pNetGame->GetVehiclePool();
        if (pVehiclePool) {
            uint16_t sNearestVehicleID = pVehiclePool->FindNearestToLocalPlayerPed();

            CVehicle *pVehicle = pVehiclePool->GetAt(sNearestVehicleID);
            if (pVehicle) {
                if (pVehicle->m_pVehicle->GetDistanceFromLocalPlayerPed() > 5.0f) return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    } else {
        return false;
    }
    return true;
}
void CRpc::vehentry() {
    CPlayerPed *pPlayerPed = pGame->FindPlayerPed();
    if(!pPlayerPed->IsInVehicle() && !pPlayerPed->IsAPassenger()) {
        CVehiclePool *pVehiclePool = pNetGame->GetVehiclePool();
        if (pVehiclePool) {
            uint16_t sNearestVehicleID = pVehiclePool->FindNearestToLocalPlayerPed();

            CVehicle *pVehicle = pVehiclePool->GetAt(sNearestVehicleID);
            if (pVehicle) {
                if (pVehicle->m_pVehicle->GetDistanceFromLocalPlayerPed() < 5.0f) {
                    CPlayerPool *pPlayerPool = pNetGame->GetPlayerPool();
                    if (pPlayerPool) {
                        CLocalPlayer *pLocalPlayer = pPlayerPool->GetLocalPlayer();
                        if (pLocalPlayer) {
                            if (!pLocalPlayer->IsSpectating()) {
                                pPlayerPed->EnterVehicle(pVehicle->m_dwGTAId, true);
                                pLocalPlayer->SendEnterVehicleNotification(sNearestVehicleID,
                                                                           true);
                            }
                        }
                    }
                }
            }
        }
    }
}

void CRpc::Packet_CustomRPC(Packet *p) {
    RakNet::BitStream bs((unsigned char *) p->data, p->length, false);
    bs.IgnoreBits(8); // skip packet id

    uint16_t rpcID;
    bs.Read(rpcID);

    FLog("RPC %d", rpcID);

    switch (rpcID) {
        case 139: {
            uint16_t length;
            bs.Read(length); // Читаем длину строки
            std::string jsonString(length, '\0');
            bs.Read(&jsonString[0], length); // Чтение строки
            FLog("RPC 139: %s", jsonString.c_str());

            uint32_t idveh;
            uint32_t turn;
            try {
                // Парсим JSON
                auto j = json::parse(jsonString);

                // Извлекаем значения нужных нам параметров
                idveh = j["i"];
                turn = j["t"];

                // Теперь мы можем использовать извлечённые значения
                /*FLog("Параметр 'i': %s", idveh);
                FLog("Параметр 't': %d", turn);*/

            } catch(const std::exception &ex) {
                FLog("Ошибка при разборе JSON: %s", ex.what());
            }

            if(pNetGame->GetVehiclePool()->GetAt(idveh) == nullptr) {
                //FLog("zxuiii");
                break;
            }
            CVehicle* veh = pNetGame->GetVehiclePool()->GetAt(idveh);
            if(pGame->getSampId(veh->m_pVehicle) == idveh) {

                //vehicle->m_iTurnState = static_cast<CVehicle::eTurnState>(turn);
                if(turn == 0) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_OFF;
                } else if (turn == 1) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_LEFT;
                } else if (turn == 2) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_RIGHT;
                } else if (turn == 3) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_ALL;
                }
            }
            break;
            //if (pUI) pUI->chat()->addDebugMessage("RPC Work EDGAR 3.0 gta 2.1 SAMP поворотники");
        }
        case 130: {
            uint16_t length;
            bs.Read(length); // Читаем длину строки
            std::string jsonString(length, '\0');
            bs.Read(&jsonString[0], length); // Чтение строки
            FLog("RPC 139: %s", jsonString.c_str());

            uint32_t idveh;
            uint32_t turn;
            try {
                // Парсим JSON
                auto j = json::parse(jsonString);

                // Извлекаем значения нужных нам параметров
                idveh = j["i"];
                turn = j["t"];

                // Теперь мы можем использовать извлечённые значения
                /*FLog("Параметр 'i': %s", idveh);
                FLog("Параметр 't': %d", turn);*/

            } catch(const std::exception &ex) {
                FLog("Ошибка при разборе JSON: %s", ex.what());
            }
            if(pNetGame->GetVehiclePool()->GetAt(idveh) == nullptr) {
                //FLog("zxuiii");
                break;
            }
            CVehicle* veh = pNetGame->GetVehiclePool()->GetAt(idveh);
            if(pGame->getSampId(veh->m_pVehicle) == idveh) {

                //vehicle->m_iTurnState = static_cast<CVehicle::eTurnState>(turn);
                if(turn == 0) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_OFF;
                } else if (turn == 1) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_LEFT;
                } else if (turn == 2) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_RIGHT;
                } else if (turn == 3) {
                    CHUD::m_iTurnState[pGame->getSampId(veh->m_pVehicle)] = CHUD::eTurnState::TURN_ALL;
                }
            }
            break;
            //if (pUI) pUI->chat()->addDebugMessage("RPC Work EDGAR 3.0 gta 2.1 SAMP поворотники");
        }
        case 99: {
            FLog("RPC_CHECK_CASH");
            uint8_t bLen, bLen1;
            uint16_t bVersion;
            char szText[30];
            char szText1[30];

            memset(szText, 0, 30);
            memset(szText1, 0, 30);

            bs.Read(bLen);
            if (bLen >= sizeof(szText) - 1)
                return;

            bs.Read(&szText[0], bLen);

            bs.Read(bLen1);
            if (bLen1 >= sizeof(szText1) - 1)
                return;

            bs.Read(&szText1[0], bLen1);

            bs.Read(bVersion);

            RwTexture *pCashTexture = nullptr;
            pCashTexture = (RwTexture *) CUtil::LoadTextureFromDB(szText1, szText);

            int iVersion;
            if (pCashTexture) {
                iVersion = bVersion;
                RwTextureDestroy(pCashTexture);
            } else iVersion = 0;

            RakNet::BitStream bsParams;

            bsParams.Write((uint8_t) 251);
            bsParams.Write((uint32_t) 99);

            bsParams.Write(iVersion);

            pNetGame->GetRakClient()->Send(&bsParams, SYSTEM_PRIORITY, RELIABLE, 0);

//			bsParams.Write(iVersion);
//			m_pRakClient->RPC(&RPC_CustomHash, &bsParams, HIGH_PRIORITY, RELIABLE, 0, false, UNASSIGNED_NETWORK_ID, NULL);
            break;
        }
        case 33: {
            uint16_t length; // Используем меньший тип данных (short)
            bs.Read(length); // Читаем длину строки

            std::string jsonString(length, '\0');
            bs.Read(&jsonString[0], length); // Чтение строки
            FLog("RPC 33: %s", jsonString.c_str());

            nlohmann::json outputJson;

            size_t jsonSize = 0;

            if (nlohmann::json::accept(jsonString)) {
                try {
                    uint32_t antiCheatCod;

                    // Парсим JSON
                    nlohmann::json jsonObj = nlohmann::json::parse(jsonString);

                    // Извлекаем значения нужных нам параметров
                    antiCheatCod = jsonObj["a"];

                    if (antiCheatCod == 26022010) {
                        antiCheatCod += 10;
                    }

                    outputJson["a"] = antiCheatCod;

                    // Конвертируем JSON в строку и получаем её длину
                    jsonString = outputJson.dump();
                    FLog("dump json: %s", jsonString.c_str());

                    FLog("Attempting to allocate memory for a block of size: %zu bytes.", jsonString.length());

                    server = true;

                } catch(const std::exception &ex) {
                    FLog("Ошибка при разборе JSON: %s", ex.what());
                }
            }

            if (!outputJson.is_null() && jsonString.length() != 0) {
                RakNet::BitStream bsSend;

                bsSend.Write((uint8_t)ID_CUSTOM_RPC);      // Магическое число
                bsSend.Write((uint8_t)33);                 // ID GUI
                bsSend.Write((uint16_t)jsonString.length()); // Длина JSON теперь тоже small integer
                bsSend.Write(jsonString.c_str(), jsonString.length()); // Данные JSON

                // И отправляем пакет
                pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);
            }
            break;
        }
        case 32:
        {
            uint16_t length;
            bs.Read(length); // Читаем длину строки
            std::string jsonString(length, '\0');
            bs.Read(&jsonString[0], length); // Чтение строки
            FLog("RPC 32: %s", jsonString.c_str());

            JNIEnv* env = g_pJavaWrapper->GetEnv();
            if(nlohmann::json::accept(jsonString)) {
                if (clazzz != NULL) {
                    jmethodID getInstanceMethod = env->GetStaticMethodID(clazzz, "getInstance",
                                                                         "()Lru/edgar/space/SAMP;");
                    if (getInstanceMethod != NULL) {
                        jobject guiManagerInstance = env->CallStaticObjectMethod(clazzz,
                                                                                 getInstanceMethod);

                        jstring jsonJString = env->NewStringUTF(
                                jsonString.c_str()); // Преобразуем строку JSON в JNI тип

                        jmethodID showCurrentGUIMethod = env->GetMethodID(clazzz,
                                                                          "onPacketIncoming",
                                                                          "(ILorg/json/JSONObject;)V");
                        if (showCurrentGUIMethod != NULL) {
                            jclass jsonObjectClass = env->FindClass("org/json/JSONObject");
                            if (jsonObjectClass != NULL) {
                                jmethodID constructor = env->GetMethodID(jsonObjectClass, "<init>",
                                                                         "(Ljava/lang/String;)V");
                                if (constructor != NULL) {
                                    jobject jsonObject = env->NewObject(jsonObjectClass,
                                                                        constructor, jsonJString);

                                    // Теперь вызываем метод Java
                                    env->CallVoidMethod(guiManagerInstance, showCurrentGUIMethod,
                                                        rpcID /* экран ID */, jsonObject);

                                    env->DeleteLocalRef(jsonObject);
                                }
                            }
                        }

                        env->DeleteLocalRef(jsonJString);
                        env->DeleteLocalRef(guiManagerInstance);
                    }
                }
            }
            break;
        }
        case 31:
        {
            uint16_t length;
            bs.Read(length); // Читаем длину строки
            std::string jsonString(length, '\0');
            bs.Read(&jsonString[0], length); // Чтение строки
            FLog("RPC 31: %s", jsonString.c_str());

            JNIEnv* env = g_pJavaWrapper->GetEnv();
            if(nlohmann::json::accept(jsonString)) {
                if (clazzz != NULL) {
                    jmethodID getInstanceMethod = env->GetStaticMethodID(clazzz, "getInstance",
                                                                         "()Lru/edgar/space/SAMP;");
                    if (getInstanceMethod != NULL) {
                        jobject guiManagerInstance = env->CallStaticObjectMethod(clazzz,
                                                                                 getInstanceMethod);

                        jstring jsonJString = env->NewStringUTF(
                                jsonString.c_str()); // Преобразуем строку JSON в JNI тип

                        jmethodID showCurrentGUIMethod = env->GetMethodID(clazzz,
                                                                          "onPacketIncoming",
                                                                          "(ILorg/json/JSONObject;)V");
                        if (showCurrentGUIMethod != NULL) {
                            jclass jsonObjectClass = env->FindClass("org/json/JSONObject");
                            if (jsonObjectClass != NULL) {
                                jmethodID constructor = env->GetMethodID(jsonObjectClass, "<init>",
                                                                         "(Ljava/lang/String;)V");
                                if (constructor != NULL) {
                                    jobject jsonObject = env->NewObject(jsonObjectClass,
                                                                        constructor, jsonJString);

                                    // Теперь вызываем метод Java
                                    env->CallVoidMethod(guiManagerInstance, showCurrentGUIMethod,
                                                        rpcID /* экран ID */, jsonObject);

                                    env->DeleteLocalRef(jsonObject);
                                }
                            }
                        }

                        env->DeleteLocalRef(jsonJString);
                        env->DeleteLocalRef(guiManagerInstance);
                    }
                }
            }
            break;
        }
    }
}