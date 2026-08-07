package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nvidia.devtech.NvEventQueueActivity;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.SAMP;

public class MenuFragment {

    public NvEventQueueActivity nvEventQueueActivity = null;
    public ViewGroup viewGroup = null;
    public FrameLayout btn_play;

    public MenuFragment(NvEventQueueActivity nvEventQueueActivity, int guiId) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
        init();
    }

    public void init() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_menu, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(this.viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        btn_play = viewGroup.findViewById(R.id.btn_play);
        btn_play.setOnClickListener(v -> {
            MainScreenActivity.nickName = "Piter_Parker";
            SAMP.getInstance().connectEdgar();
            hide();
        });
        ((TextView) viewGroup.findViewById(R.id.uidtext)).setText(SAMP.getInstance().mAuth.getUid());
        btn_play.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, btn_play));
        viewGroup.setLayoutParams(layoutParams);
        viewGroup.setVisibility(View.VISIBLE);
    }

    public void hide() {
        viewGroup.setVisibility(View.GONE);
    }
}
