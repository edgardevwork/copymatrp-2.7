package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.SAMP;

public class ProfileFragment {

    private ViewGroup viewGroup;

    public ProfileFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_profile, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        viewGroup.setVisibility(View.GONE);

        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.VISIBLE);
    }
}
