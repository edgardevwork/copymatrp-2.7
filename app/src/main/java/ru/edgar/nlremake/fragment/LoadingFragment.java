package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;

public class LoadingFragment {

    private ViewGroup viewGroup;

    private static LoadingFragment instance;

    private ImageView progress_loading;

    public LoadingFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_loading, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        instance = this;

        progress_loading = viewGroup.findViewById(R.id.progress_loading);
        progress_loading.startAnimation(AnimationUtils.loadAnimation(MainScreenActivity.getInstance(), R.anim.rotate_animation));

        viewGroup.setVisibility(View.GONE);
    }

    public static LoadingFragment getInstance() {
        return instance;
    }

    public void show() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void hide() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }
}
