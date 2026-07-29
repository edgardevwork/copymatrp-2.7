#pragma once

#include "rwcore.h"
#include "Rect.h"

struct CGtaRect
{
public:
	float x1;           // x1
	float y1;    // y1

//	float x2;        // x2
//	float y2;          // y2
};

struct CRadarRect
{
public:
    float x1;           // x1
    float y1;    // y1

    float x2;        // x2
    float y2;          // y2
};

class CHUD
{
public:
    static bool m_bShow;

public:
    static void Initialise();

    static CGtaRect radarBgPos1; // x y
    static CGtaRect radarBgPos2; // x y
    static CRadarRect radar1; // x y

    static void Disable()      { m_bShow = false; }
    static void Enable()       { m_bShow = true; };

    static bool IsEnabled()    { return m_bShow; };

    static void Render();

    static CVehicle* vehicles[MAX_VEHICLES];

    enum eTurnState
    {
        TURN_OFF,
        TURN_LEFT,
        TURN_RIGHT,
        TURN_ALL
    };

    static eTurnState GetState(int i)  {
        return m_iTurnState[i];
    }

    static eTurnState 		m_iTurnState[MAX_VEHICLES];
};
