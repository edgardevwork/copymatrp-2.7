#include "../gui.h"
#include "../../main.h"
#include "../../game/game.h"
#include "../../net/netgame.h"
#include <algorithm>
#include "../settings.h"
#include "java/jniutil.h"

extern UI* pUI;
extern CGame* pGame;
extern CNetGame* pNetGame;
extern CSettings* pSettings;
extern CJavaWrapper *pJavaWrapper;

extern "C" {
JNIEXPORT void JNICALL Java_com_nvidia_devtech_NvEventQueueActivity_OpenKeyBoarClient(JNIEnv * env , jobject thiz ) {

    /*if (pKeyBoard)
    {
        pKeyBoard->Open(&DialogWindowInputHandler, true);
        Log("опен");
    }*/
    //pChatWindow->OOpen();

}
JNIEXPORT void JNICALL Java_ru_edgar_space_core_ui_chatedgar_ChatManager_sendChatMessages(JNIEnv* pEnv, jobject thiz, jbyteArray str) {
    jboolean isCopy = true;

    jbyte* pMsg = pEnv->GetByteArrayElements(str, &isCopy);
    jsize length = pEnv->GetArrayLength(str);

    std::string szStr((char*)pMsg, length);

    /*if(pNetGame) {

    }*/

    Chat::keyboardEvent(szStr.c_str());

    pEnv->ReleaseByteArrayElements(str, pMsg, JNI_ABORT);
}
}


Chat::Chat()
	: ListBox()
{

}

void Chat::addChatMessage(const std::string& message, const std::string& nick, const ImColor& nick_color)
{
	addPlayerMessage(message, nick, nick_color);
}

void Chat::addInfoMessage(const std::string& format, ...)
{
	char tmp_buf[512];

	va_list args;
	va_start(args, format);
	vsprintf(tmp_buf, format.c_str(), args);
	va_end(args);

	addMessage(std::string(tmp_buf), ImColor(0x00, 0xc8, 0xc8));
}

void Chat::addDebugMessage(const std::string& format, ...)
{
	char tmp_buf[512];

	va_list args;
	va_start(args, format);
	vsprintf(tmp_buf, format.c_str(), args);
	va_end(args);

	addMessage(std::string(tmp_buf), ImColor(0xbe, 0xbe, 0xbe));
}

void Chat::addClientMessage(const std::string& message, const ImColor& color)
{
	addMessage(message, color);
}

void Chat::FilterInvalidChars(char *szString)
{
    while(*szString)
    {
        if(*szString > 0 && *szString < ' ')
            *szString = ' ';

        szString++;
    }
}

void Chat::addMessage(const std::string& message, const ImColor& color)
{
    //std::string m = windows1251ToUTF8(message);
    // Если нужно создать изменяемый массив char
    char msg[100000]; // Убедитесь, что размер достаточен для строки
    std::strcpy(msg, message.c_str()); // Копируем строку
    Chat::FilterInvalidChars(msg);
    FLog(msg);
    // Преобразуем ImColor в int (например, ARGB)
    int colorInt = (static_cast<int>(color.Value.w * 255) << 24) | // Alpha
               (static_cast<int>(color.Value.x * 255) << 16) | // Red
               (static_cast<int>(color.Value.y * 255) << 8)  | // Green
               (static_cast<int>(color.Value.z * 255));        // Blue
    if (ProcessVoiceCommands(msg))
    {
        return;
    } else
        pJavaWrapper->AddChatMessage(msg, colorInt);
}
void FilterInvalidChars(char *szString)
{
    while(*szString)
    {
        if(*szString > 0 && *szString < ' ')
            *szString = ' ';

        szString++;
    }
}

void Chat::addPlayerMessage(const std::string& message, const std::string& nick, const ImColor& nick_color)
{
	if (this->itemsCount() > UISettings::chatMaxMessages())
	{
		this->removeItem(0);
	}

	PlayerMessageItem* item = new PlayerMessageItem(message, nick, nick_color);
	this->addItem(item);
	/*if(!active())*/ this->setScrollY(1.0f);
}

void Chat::draw(ImGuiRenderer* renderer)
{
	ListBox::draw(renderer);
}

void Chat::activateEvent(bool active)
{
	if (active)
	{
		this->setScrollable(true);
	}
	else
	{
		this->setScrollable(false);
	}
}

void Chat::touchPopEvent()
{
	if (pUI->playertablist()->visible()) return;

	//pUI->keyboard()->show(this);
}

void Chat::keyboardEvent(const char* str)
{
	if(!str || *str == '\0') return;
        if(!pNetGame) return;
        FLog("pon %s", str);

        if (*str == '/')
        {
            if (ProcessVoiceCommands(str))
            {
                return;
            }
            pNetGame->SendChatCommand(str);
        } else pNetGame->SendChatMessage(str);
}
bool Chat::ProcessVoiceCommands(const char* str)
{
    FLog(str);
    if (strstr(str, "/SPAWNponEDdgahvdjkhg937"))
    {
        pJavaWrapper->ShowSpawn();
        return true;
    }
    return false;
}