#include "../main.h"
#include "../game/game.h"
#include "../net/netgame.h"
#include "gui.h"
#include "../playertags.h"
#include "../net/playerbubblepool.h"
#include "vendor/str_obfuscator/str_obfuscator.hpp"
// voice
#include "../voice_new/Plugin.h"
#include "../voice_new/MicroIcon.h"
#include "../voice_new/SpeakerList.h"
#include "../voice_new/Network.h"

#include "../gui/samp_widgets/voicebutton.h"
#include "game/Textures/TextureDatabaseRuntime.h"
#include "game/Streaming.h"
#include "game/Pools.h"

extern CNetGame* pNetGame;
extern CPlayerTags* pPlayerTags;
extern UI* pUI;

UI::UI(const ImVec2& display_size, const std::string& font_path)
	: Widget(), ImGuiWrapper(display_size, font_path)
{
	UISettings::Initialize(display_size);
	this->setFixedSize(display_size);
}
extern CGame* pGame;
uint32_t UI::uiStreamedObject = 0;
bool UI::initialize()
{
	if (!ImGuiWrapper::initialize()) return false;

	m_splashScreen = new SplashScreen();
	this->addChild(m_splashScreen);
	m_splashScreen->setFixedSize(size());
	m_splashScreen->setPosition(ImVec2(0.0f, 0.0f));
	m_splashScreen->setVisible(true);

	m_chat = new Chat();
	this->addChild(m_chat);
	m_chat->setFixedSize(UISettings::chatSize());
	m_chat->setPosition(UISettings::chatPos());
	m_chat->setItemSize(UISettings::chatItemSize());
	m_chat->setVisible(false);

	m_buttonPanel = new ButtonPanel();
	this->addChild(m_buttonPanel);
	m_buttonPanel->setFixedSize(UISettings::buttonPanelSize());
	m_buttonPanel->setPosition(UISettings::buttonPanelPos());
	m_buttonPanel->setVisible(false);

	m_voiceButton = new VoiceButton();
	this->addChild(m_voiceButton);
	m_voiceButton->setFixedSize(UISettings::buttonVoiceSize());
	m_voiceButton->setPosition(UISettings::buttonVoicePos());
	m_voiceButton->setVisible(false);

	m_spawn = new Spawn();
	this->addChild(m_spawn);
	m_spawn->setFixedSize(UISettings::spawnSize());
	m_spawn->setPosition(UISettings::spawnPos());
	m_spawn->setVisible(false);

	m_dialog = new Dialog();
	this->addChild(m_dialog);
	m_dialog->setVisible(false);
	m_dialog->setMinSize(UISettings::dialogMinSize());
	m_dialog->setMaxSize(UISettings::dialogMaxSize());

	m_keyboard = new Keyboard();
	this->addChild(m_keyboard);
	m_keyboard->setFixedSize(UISettings::keyboardSize());
	m_keyboard->setPosition(UISettings::keyboardPos());
	m_keyboard->setVisible(false);

	m_playerTabList = new PlayerTabList();
	this->addChild(m_playerTabList);
	m_playerTabList->setMinSize(UISettings::dialogMinSize());
	m_playerTabList->setMaxSize(UISettings::dialogMaxSize());
	m_playerTabList->setVisible(false);

    label = new Label(" ", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
    pUI->addChild(label);

    label2 = new Label(" ", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
    pUI->addChild(label2);

    label3 = new Label(" ", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
    pUI->addChild(label3);

    label4 = new Label(" ", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
    pUI->addChild(label4);

	// mem
	Label* d_label1;

    char text3[228];

    sprintf(text3, "SA:MP Mobile 2.10 x64 By EDGAR 3.0 space 2.7\nStreamed Objects: %d", uiStreamedObject);

	d_label1 = new Label(text3, ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 3);
	//this->addChild(d_label1);
	//d_label1->setPosition(ImVec2(3.0, 3.0));
    // ==== version ==== //
    //d_label = new Label("", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
    //this->addChild(d_label);
   // d_label->setPosition(ImVec2(3.0, 55.0));

	return true;
}

float EntityGetDistanceFromCamera(PON_TYPE* m_pEntity)
{
    RwMatrix	matFromPlayer;
    RwMatrix	matThis;
    float 		fSX, fSY, fSZ;

    CPlayerPed *pLocalPlayerPed = pGame->FindPlayerPed();
    CLocalPlayer *pLocalPlayer  = nullptr;

    if(!pLocalPlayerPed) return 10000.0f;
    pLocalPlayerPed->GetMatrix(&matThis);

    if(pNetGame)
    {
        pLocalPlayer = pNetGame->GetPlayerPool()->GetLocalPlayer();
        if(pLocalPlayer && (pLocalPlayer->IsSpectating() || pLocalPlayer->IsInRCMode()))
        {
            pLocalPlayerPed->GetMatrix(&matFromPlayer);
        }
        else
        {
            pLocalPlayerPed->GetMatrix(&matFromPlayer);
        }
    }
    else
    {
        pLocalPlayerPed->GetMatrix(&matFromPlayer);
    }

    fSX = (matFromPlayer.pos.x) * (matThis.pos.x);
    fSY = (matFromPlayer.pos.y) * (matThis.pos.y);
    fSZ = (matFromPlayer.pos.z) * (matThis.pos.z);

    return (float)sqrt(fSX + fSY + fSZ);
}

#include <algorithm>
#include <string>
std::string ReplaceAll(std::string str, const std::string& from, const std::string& to) {
    size_t start_pos = 0;
    while ((start_pos = str.find(from, start_pos)) != std::string::npos) {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length();
    }

    return str;
}

void UI::render()
{
	ImGuiWrapper::render();

    //renderDebug();

    ProcessPushedTextdraws();

    if(pGame) UI::ShowSpeed();

	if (m_bNeedClearMousePos) {
		ImGuiIO& io = ImGui::GetIO();
		io.MousePos = ImVec2(-1, -1);
		m_bNeedClearMousePos = false;
	}
}
#include "java/jniutil.h"
extern CJavaWrapper *pJavaWrapper;

void UI::ShowSpeed(){
    if (!pGame || !pNetGame || !pGame->FindPlayerPed()->IsInVehicle()) {
        pJavaWrapper->HideSpeed();
        //bMeliage =0;
       // m_fuel = 0;
        return;
    }
    if (pGame->FindPlayerPed()->IsAPassenger()) {
        pJavaWrapper->HideSpeed();
       // bMeliage =0;
       // m_fuel = 0;
        return;
    }

    int i_speed = 0;
   // bDoor =0;
    //bEngine = 0;
    //bLights = 0;
    float fHealth = 0;
    CVehicle *pVehicle = nullptr;
    CVehiclePool *pVehiclePool = pNetGame->GetVehiclePool();
    CPlayerPed *pPlayerPed = pGame->FindPlayerPed();
    VEHICLEID id = pVehiclePool->FindIDFromGtaPtr(pPlayerPed->GetGtaVehicle());
    pVehicle = pVehiclePool->GetAt(id);

    if(pPlayerPed)
    {
        if(pVehicle)
        {
            VECTOR vecMoveSpeed;
            pVehicle->GetMoveSpeedVector(&vecMoveSpeed);
            i_speed = sqrt((vecMoveSpeed.X * vecMoveSpeed.X) + (vecMoveSpeed.Y * vecMoveSpeed.Y) + (vecMoveSpeed.Z * vecMoveSpeed.Z)) * 180;
            //bHealth = pVehicle->GetHealth();
            //bDoor = pVehicle->GetDoorState();
            //bEngine = pVehicle->GetEngineState();
            //bLights = pVehicle->GetLightsState();
        }
    }
    pJavaWrapper->ShowSpeed();
    pJavaWrapper->UpdateSpeedInfo(i_speed, 0, 0, 0, 0, 0, 0, 0);
}

void UI::shutdown()
{
	ImGuiWrapper::shutdown();
}

void UI::drawList()
{
	if (!visible()) return;

	/*Label* label;
	label = new Label("1.0.11", ImColor(1.0f, 1.0f, 1.0f), true, UISettings::fontSize() / 2);
	label->setPosition(ImVec2(0.0, 0.0));
	this->addChild(label);*/

	if (pPlayerTags) pPlayerTags->Render(renderer());
	if (pNetGame && pNetGame->GetTextLabelPool()) pNetGame->GetTextLabelPool()->Render(renderer());
	if (pNetGame && pNetGame->GetPlayerBubblePool()) pNetGame->GetPlayerBubblePool()->Render(renderer());

	draw(renderer());
}

void UI::touchEvent(const ImVec2& pos, TouchType type)
{
	/* 
		� ������� ����������
		1 - ����������
		2 - ������
		3 - ���
	*/

	if (m_keyboard->visible() && m_keyboard->contains(pos))
	{
		m_keyboard->touchEvent(pos, type);
		return;
	}

	if (m_dialog->visible() && m_dialog->contains(pos))
	{
		m_dialog->touchEvent(pos, type);
		return;
	}

	Widget::touchEvent(pos, type);
}

enum eTouchType
{
	TOUCH_POP = 1,
	TOUCH_PUSH = 2,
	TOUCH_MOVE = 3
};

bool UI::OnTouchEvent(int type, bool multi, int x, int y)
{
	ImGuiIO& io = ImGui::GetIO();

	/*
	switch (type)
	{
	case 1://TOUCH_PUSH:
		io.MousePos = ImVec2(x, y);
		io.MouseDown[0] = true;
		MyLog2("TOUCH_PUSH");
		break;

	case 2://TOUCH_POP:
		io.MouseDown[0] = false;
		m_bNeedClearMousePos = true;
		MyLog2("TOUCH_POP");
		break;

	case 3://TOUCH_MOVE:
		io.MousePos = ImVec2(x, y);
		MyLog2("TOUCH_MOVE");
		break;
	}*/
	VoiceButton* vbutton = pUI->voicebutton();
	switch (type)
	{
	case TOUCH_PUSH:
		io.MousePos = ImVec2(x, y);
		io.MouseDown[0] = true;
		break;

	case TOUCH_POP:
		io.MouseDown[0] = false;
		m_bNeedClearMousePos = true;
		break;

	case TOUCH_MOVE:
		io.MousePos = ImVec2(x, y);
		//if (vbutton->countdown > 50) vbutton->countdown = 20;
		break;
	}

	return true;
}


float EntityGetDistanceFromCameraXYZ(float x, float y, float z)
{
    /*RwMatrix	matFromPlayer;
    RwMatrix	matThis;
    float 		fSX, fSY, fSZ;

    CPlayerPed *pLocalPlayerPed = pGame->FindPlayerPed();
    CLocalPlayer *pLocalPlayer  = nullptr;

    if(!pLocalPlayerPed) return 10000.0f;

    GetMatrix(&matThis);

    if(pNetGame)
    {
        pLocalPlayer = pNetGame->GetPlayerPool()->GetLocalPlayer();
        if(pLocalPlayer && (pLocalPlayer->IsSpectating() || pLocalPlayer->IsInRCMode()))
        {
            pGame->GetCamera()->GetMatrix(&matFromPlayer);
        }
        else
        {
            pLocalPlayerPed->GetMatrix(&matFromPlayer);
        }
    }
    else
    {
        pLocalPlayerPed->GetMatrix(&matFromPlayer);
    }

    fSX = (matThis.pos.x - matFromPlayer.pos.x) * (matThis.pos.x - matFromPlayer.pos.x);
    fSY = (matThis.pos.y - matFromPlayer.pos.y) * (matThis.pos.y - matFromPlayer.pos.y);
    fSZ = (matThis.pos.z - matFromPlayer.pos.z) * (matThis.pos.z - matFromPlayer.pos.z);

    return (float)sqrt(fSX + fSY + fSZ);
    FLog("%f", *(float*)(SA_ADDR(VER_x32 ? 0x2D9844 : 0x2D9828)));

    //Log("OnClickMap: %f, %f, %f", tmpX, tmpY, tmpZ);

    return sqrt(tmpX * tmpX + tmpY * tmpY + tmpZ * tmpZ);*/
    return 0.0f;
}


void UI::DrawTextureInfo(uintptr_t pEntity) {
    PON_TYPE * sEntity = (PON_TYPE*)pEntity;
    if (!sEntity->mat) {
        return;
    }

    uint16_t usEntityID = *(uint16_t*)(pEntity + 34);
    uintptr_t* pTexturesInfo = (uintptr_t*)(g_libGTASA + 0x6706E4);/*
 * .bss:006706E4                 % 1
.bss:006706E5                 % 1
.bss:006706E6                 % 1
.bss:006706E7                 % 1
.bss:006706E8 byte_6706E8     % 1                     ; DATA XREF: CRunningScript::ProcessCommands0To99(int)+22C↑r
 */
    uintptr_t pTextureInfo = (uintptr_t)pTexturesInfo[usEntityID];

    if (pTextureInfo)
    {
        char szTextureBufferInfo[0xFF] = { '\0' };
        char szTextureBuffer[0xFF] = { '\0' };

        /*float dff_size = (float)((float)((float)(pTextureInfo << 11) * 0.00097656) * 0.00097656);
        float tex_size = (float)((float)((float)pTextureInfo * 0.00097656) * 0.00097656);*/

        sprintf(szTextureBufferInfo, "Network ID: %d\nPosition: %.2f, %.2f, %.2f",usEntityID, sEntity->mat->x, sEntity->mat->y, sEntity->mat->z);

        //sprintf(szTextureBufferInfo, "DFF: %f MB\nTextures: %f MB\nID: %d\nPosition: %.2f, %.2f, %.2f\n",
        //	dff_size,
        //	 tex_size,
        //	  usEntityID,
        //	   sEntity->mat->pos.X,
        //	    sEntity->mat->pos.Y,
        //	     sEntity->mat->pos.Z);

        std::string s(szTextureBufferInfo);
        std::string from("4095");
        std::string to("4");
        std::string repl = ReplaceAll(s, from, to);
        strcpy(szTextureBufferInfo, repl.c_str());

        VECTOR TagPos;
        TagPos.X = sEntity->mat->x;
        TagPos.Y = sEntity->mat->y;
        TagPos.Z = sEntity->mat->z;
        TagPos.Z += 0.25f + (EntityGetDistanceFromCamera(sEntity) * 0.0475f);

        VECTOR Out;

        // CSprite::CalcScreenCoors
        ((void (*)(VECTOR*, VECTOR*, float*, float*, bool, bool))(SA_ADDR(VER_x32 ? 0x5C57E8 + 1 : 0x6E9DF8)))(&TagPos, &Out, 0, 0, 0, 0);

        if (Out.Z < 10.0f) {
            return;
        }

        ImVec2 pos = ImVec2(Out.X, Out.Y);
        pUI->RenderText(pos, 0xFFFFFFFF, true, szTextureBufferInfo);

    }
}

#include "..//sprite2d.h"
#include "..//util.h"

void UI::ShowMap(float x, float y, float z) {
    //FLog("[CGUI::ShowMap] Draw map");
    VECTOR TagPos;
    TagPos.X = x;
    TagPos.Y = y + 800.0f;
    TagPos.Z = z;
    TagPos.Z += (0.25f + EntityGetDistanceFromCameraXYZ(TagPos.X, TagPos.Y, TagPos.Z) * 0.0475f);

    //FLog("пон %f", TagPos.Y);
    VECTOR Out;

    if (Out.Z < 10.0f) {
        return;
    }

    // CSprite::CalcScreenCoors
    ((void (*)(VECTOR*, VECTOR*, float*, float*, bool, bool))(SA_ADDR(VER_x32 ? 0x5C57E8 + 1 : 0x6E9DF8)))(&TagPos, &Out, 0, 0, 0, 0);
    //FLog("пон");
    ImVec2 pos = ImVec2(Out.X, Out.Y);

    auto* map = new CSprite2d();
    map->m_pTexture = (RwTexture*)CUtil::LoadTextureFromDB("txd", "radar_waypoint");
    //radar->m_pRwTexture = CHUD::hud_radar;

    CRGBA color;
    color.a = 255;
    color.r = 255;
    color.g = 255;
    color.b = 255;

    map->Draw(pos.x, pos.y, 100.0f, 100.0f, &color);
    //FLog("pohn = %f %f", Out.X, Out.Y);
}

#include "../settings.h"
extern CGame *pGame;
extern CSettings* pSettings;
void UI::renderDebug()
{
    uint32_t ms_nNoOfVisibleEntities = *(uint32_t*)(g_libGTASA + 0xBCF8E4); //.bss:008C162C                 EXPORT _ZN9CRenderer23ms_nNoOfVisibleEntitiesE
    if (!ms_nNoOfVisibleEntities) {
        return;
    }

    uintptr_t* ms_aVisibleEntityPtrs = (uintptr_t*)(g_libGTASA + 0xBCF900);//.bss:008C0680                 EXPORT _ZN9CRenderer21ms_aVisibleEntityPtrsE
  //  .bss:008C0680 ; CRenderer::ms_aVisibleEntityPtrs
        //    .bss:008C0680 _ZN9CRenderer21ms_aVisibleEntityPtrsE % 4

    do {
        uintptr_t m_pEntityPointer = (uintptr_t)ms_aVisibleEntityPtrs[ms_nNoOfVisibleEntities];
        if (m_pEntityPointer) {
            DrawTextureInfo(m_pEntityPointer);
        }
        --ms_nNoOfVisibleEntities;
    } while (ms_nNoOfVisibleEntities);
    if(!pSettings->Get().iFPSCounter) return;

    char szStr[30];
    char szStrMem[64];
    char szStrPos[64];

    ImVec2 pos = ImVec2(pUI->ScaleX(40.0f), pUI->ScaleY(540.0f));

    static float fps = 120.f;
        static auto lastTick = CTimer::m_snTimeInMillisecondsNonClipped;
        if(CTimer::m_snTimeInMillisecondsNonClipped - lastTick > 500) {
            lastTick = CTimer::m_snTimeInMillisecondsNonClipped;
            fps = std::clamp(CTimer::game_FPS, 10.f, (float) 120);
        }
        snprintf(&szStr[0], sizeof(szStr), "FPS: %.0f", fps);

        label->setText(&szStr[0]);
        label->setPosition(pos);

        /*auto &msUsed = CStreaming::ms_memoryUsed;
        auto &msAvailable = CStreaming::ms_memoryAvailable;

        struct mallinfo memInfo = mallinfo();
        int totalAllocatedMB  = memInfo.uordblks / (1024 * 1024);

        snprintf(&szStrMem[0], sizeof(szStrMem), "MEM: %d mb (stream %d/%d) (Tex %d MB)",
                 totalAllocatedMB,
                 msUsed / (1024 * 1024),
                 msAvailable / (1024 * 1024),
                 TextureDatabaseRuntime::storedTexels / (1024 * 1024)
        );

        pos = ImVec2(pUI->ScaleX(40.0f), pUI->ScaleY(1080.0f - UISettings::fontSize() * 9));

        label2->setText(&szStrMem[0]);
        label2->setPosition(pos);

        if (pGame->FindPlayerPed()->m_pPed)
        {
            snprintf(&szStrPos[0], sizeof(szStrPos), "POS: %.2f, %.2f, %.2f", pGame->FindPlayerPed()->m_pPed->m_matrix->m_pos.x, pGame->FindPlayerPed()->m_pPed->m_matrix->m_pos.y, pGame->FindPlayerPed()->m_pPed->m_matrix->m_pos.z);
            pos = ImVec2(pUI->ScaleX(40.0f), pUI->ScaleY(1080.0f - UISettings::fontSize() * 8));
            label3->setText(&szStrPos[0]);
            label3->setPosition(pos);
        }
        //Log("pools = %d mem = %d", GetPedPoolGta()->GetNoOfUsedSpaces(), totalAllocatedMB);
        char debugPools[250];
        snprintf(&debugPools[0], sizeof(debugPools), "NSingle: %d; NDouble: %d; Peds: %d; Veh's: %d; Obj: %d; EntryInf: %d; Dummies: %d, Buildings: %d",
                 GetPtrNodeSingleLinkPool()->GetNoOfUsedSpaces(),
                 GetPtrNodeDoubleLinkPool()->GetNoOfUsedSpaces(),
                 GetPedPoolGta()->GetNoOfUsedSpaces(),
                 GetVehiclePoolGta()->GetNoOfUsedSpaces(),
                 GetObjectPoolGta()->GetNoOfUsedSpaces(),
                 GetEntryInfoNodePool()->GetNoOfUsedSpaces(),
                 GetDummyPool()->GetNoOfUsedSpaces(),
                 GetBuildingPool()->GetNoOfUsedSpaces()
                 );

        pos = ImVec2(pUI->ScaleX(40.0f), pUI->ScaleY(1080.0f - UISettings::fontSize() * 1));

        label4->setText(&debugPools[0]);
        label4->setPosition(pos);*/
}


void UI::RenderText(ImVec2& posCur, ImU32 col, bool bOutline, const char* text_begin, const char* text_end)
{
    int iOffset = pSettings->GetReadOnly().iFontOutline;

    if (bOutline)
    {
            posCur.x -= iOffset;
            ImGui::GetBackgroundDrawList()->AddText(posCur, ImColor(IM_COL32_BLACK), text_begin, text_end);
            posCur.x += iOffset;
            // right
            posCur.x += iOffset;
            ImGui::GetBackgroundDrawList()->AddText(posCur, ImColor(IM_COL32_BLACK), text_begin, text_end);
            posCur.x -= iOffset;
            // above
            posCur.y -= iOffset;
            ImGui::GetBackgroundDrawList()->AddText(posCur, ImColor(IM_COL32_BLACK), text_begin, text_end);
            posCur.y += iOffset;
            // below
            posCur.y += iOffset;
            ImGui::GetBackgroundDrawList()->AddText(posCur, ImColor(IM_COL32_BLACK), text_begin, text_end);
            posCur.y -= iOffset;
    }

    ImGui::GetBackgroundDrawList()->AddText(posCur, col, text_begin, text_end);
}

void UI::PushToBufferedQueueTextDrawPressed(uint16_t textdrawId)
{
    BUFFERED_COMMAND_TEXTDRAW* pCmd = m_BufferedCommandTextdraws.WriteLock();

    pCmd->textdrawId = textdrawId;

    m_BufferedCommandTextdraws.WriteUnlock();
}

void UI::ProcessPushedTextdraws()
{
    BUFFERED_COMMAND_TEXTDRAW* pCmd = nullptr;
    while (pCmd = m_BufferedCommandTextdraws.ReadLock())
    {
        RakNet::BitStream bs;
        bs.Write(pCmd->textdrawId);
        pNetGame->GetRakClient()->RPC(&RPC_ClickTextDraw, &bs, HIGH_PRIORITY, RELIABLE_SEQUENCED, 0, false, UNASSIGNED_NETWORK_ID, 0);
        m_BufferedCommandTextdraws.ReadUnlock();
    }
}

