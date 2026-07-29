#include <jni.h>
#include "../../main.h"
#include "../../game/game.h"
#include "../../net/netgame.h"
#include "CAntiCheat.h"
extern CGame *pGame;
extern CNetGame* pNetGame;

#define RPC_ANTICHEAT      0x33

void CAntiCheat::sendMod()
{
    //Chat::keyboardEvent("/adtest");
    std::string jsonString = "{\"a\":26022020}";
    FLog("Auth");
    FLog(jsonString.c_str());
    RakNet::BitStream bsSend;

    bsSend.Write((uint8_t)ID_CUSTOM_RPC);      // Магическое число
    bsSend.Write((uint8_t)33);                 // ID GUI
    bsSend.Write((uint16_t)jsonString.length()); // Длина JSON теперь тоже small integer
    bsSend.Write(jsonString.c_str(), jsonString.length()); // Данные JSON

    // И отправляем пакет
    pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);
    /*FLog("CAntiCheat: sendMod");

    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)ID_CUSTOM_RPC);
    bsSend.Write((uint32_t)RPC_ANTICHEAT);
    bsSend.Write((uint32_t)237);

    pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);*/

}

/*extern "C" {

    JNIEXPORT void JNICALL Java_ru_edgar_space_core_ui_chatedgar_ChatManager_sendChatMessages(JNIEnv* pEnv, jobject thiz, jbyteArray str) {
    }
}*/