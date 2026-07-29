#include "main.h"
#include "game/game.h"
#include "net/netgame.h"
#include "gui/gui.h"
#include "CHUD.h"
//#include "util/patch.h"
#include <string>
#include <jni.h>

// GTASA HOOK
#include "game/Models/ModelInfo.h"
#include "game/RW/rwcore.h"
#include "game/RW/rpworld.h"
#include "game/RW/RenderWare.h"

extern CGame* pGame;
extern CNetGame *pNetGame;
extern UI *pUI;

CHUD::eTurnState 		CHUD::m_iTurnState[MAX_VEHICLES];

bool CHUD::m_bShow = false;

CGtaRect CHUD::radarBgPos1; // X Y
CGtaRect CHUD::radarBgPos2; // X Y
CRadarRect CHUD::radar1; // x y x2 y2

void CHUD::Initialise()
{   
    FLog("CHUD: Initialise");

    FLog("CHUD: Loading..");
}

extern "C"
{
    JNIEXPORT void JNICALL Java_ru_edgar_space_SAMP_SetRadarBgPos(JNIEnv *env, jobject thiz, jfloat x1, jfloat y1, jfloat x2, jfloat y2)
    {
        // -- обложка
        CHUD::radarBgPos1.x1 = x1;
        CHUD::radarBgPos1.y1 = y1;

        CHUD::radarBgPos2.x1 = x2;
        CHUD::radarBgPos2.y1 = y2;

        // -- радар
        //                                    CHUD::radar1.x1 = x1;
        //		CHUD::radar1.y1 = y1;
    }

    JNIEXPORT void JNICALL Java_ru_edgar_space_SAMP_SetRadarPos(JNIEnv *env, jobject thiz, jfloat x1, jfloat y1, jfloat x2, jfloat y2)
    {
        CHUD::radar1.x1 = x1;
        CHUD::radar1.y1 = y1;

        CHUD::radar1.x2 = x2;
        CHUD::radar1.y2 = y2;
    }

    JNIEXPORT void JNICALL Java_ru_edgar_space_SAMP_SetRadarEnabled(JNIEnv *env, jobject thiz, jboolean tf)
    {
        if(tf)
        {
            CHUD::Enable();
        }
        else
        {
            CHUD::Disable();
        }
    }
}

void CHUD::Render()
{
    if(CHUD::IsEnabled())
    {
        FLog("CHUD: Render");

    } else FLog("CHUD: NoRender");
}