package ru.edgar.nlremake.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;

public class LoadingFragment implements LauncherUiComponent {

    private ViewGroup viewGroup;
    private ImageView progress_loading;

    @Override
    public void init(Activity activity) {
        if(viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_loading, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        progress_loading = viewGroup.findViewById(R.id.progress_loading);
        progress_loading.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate_animation));

        viewGroup.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
