package ru.edgar.space.core.ui.spawnmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ru.edgar.space.UiManager;
import ru.edgar.matrp.R;
import ru.edgar.space.SAMP;

public class SpawnMenu {

    public Activity activity = null;
    public ViewGroup viewGroup = null;
    public LinearLayout[] cont = new LinearLayout[9];
    public boolean is[] = new boolean[9];

    public SpawnMenu(Activity activity, int guiId) {
        this.activity = activity;
        //viewGroup = UiManager.getUiManager().viewGroup[guiId];
        init();
        viewGroup.setVisibility(View.GONE);
        //UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }

    public void init() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spawnmenu, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(this.viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        for (int i10 = 0; i10 < 8; i10++) {
            cont[i10] = (LinearLayout) viewGroup.findViewById(viewGroup.getResources().getIdentifier("sm_button_" + i10, "id", activity.getPackageName()));
            ((LinearLayout) cont[i10]).setOnTouchListener(new UiManager.animClickBtn(SAMP.getInstance(), cont[i10]));
            is[i10] = false;
        }
        clikc(1);
    }
    public void clikc(int id) {
        for (int i10 = 0; i10 < 8; i10++) {
            if (i10 == id) {
                if (!is[i10]) {
                    is[i10] = true;
                    ((LinearLayout) cont[i10]).setVisibility(View.VISIBLE);
                    ((LinearLayout) cont[i10]).setBackground(viewGroup.getResources().getDrawable(R.drawable.bankomat_main_item_bg));
                    ((TextView) cont[i10].getChildAt(1)).setTextColor(Color.parseColor("#FFFFFF"));
                    int finalI1 = i10;
                    ((LinearLayout) cont[i10]).setOnTouchListener(new UiManager.animClickBtn(SAMP.getInstance(), cont[i10]));
                    ((LinearLayout) cont[i10]).setOnClickListener(v -> {
                        clikc(finalI1);
                    });
                    if (i10 == 6) {
                        ((LinearLayout) cont[i10]).setVisibility(View.GONE);
                    }
                } else {
                    UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("i", id);
                        SAMP.getInstance().sendJsonData(32, jSONObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                ((LinearLayout) cont[i10]).setVisibility(View.VISIBLE);
                ((LinearLayout) cont[i10]).setBackground(viewGroup.getResources().getDrawable(R.drawable.spawnmenu_blocked_bg));
                ((TextView) cont[i10].getChildAt(1)).setTextColor(Color.parseColor("#33FFFFFF"));
                ((LinearLayout) cont[i10]).setOnTouchListener(new UiManager.animClickBtn(SAMP.getInstance(), cont[i10]));
                int finalI2 = i10;
                ((LinearLayout) cont[i10]).setOnClickListener(v -> {
                    clikc(finalI2);
                });
                if (i10 == 6) {
                    ((LinearLayout) cont[i10]).setVisibility(View.GONE);
                }
                is[i10] = false;
            }
        }
    }
    public void ShowSpawnMenu() {
        //InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.VISIBLE);
        if (viewGroup == null) {
            System.err.println("viewGroup is null when trying to show menu");
            return;
        }
        if (!UiManager.getUiManager().getKeyBoardManager().isChatClose) {
            UiManager.getUiManager().getKeyBoardManager().x();
        } else {
            UiManager.getUiManager().getKeyBoardManager().q();
        }
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void HideSpawnMenu() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
