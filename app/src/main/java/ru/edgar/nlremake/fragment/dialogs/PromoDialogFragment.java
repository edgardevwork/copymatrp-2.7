package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.fragment.LoadingFragment;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.space.UiManager;

public class PromoDialogFragment {
    private ViewGroup viewGroup;
    private Activity context;
    private FrameLayout btn_back, main_btn_yes;
    private LoadingFragment loadingFragment;

    public PromoDialogFragment(Activity activity) {
        if(viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        context = activity;

        viewGroup = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog_promo, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        loadingFragment = UiManager.getUiManager().getTyped(UiManager.LOADING);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(context, btn_back));
        btn_back.setOnClickListener(v -> {
            hidePromoDialog();
        });

        main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
        main_btn_yes.setOnTouchListener(new UiManager.animClickBtn(context, main_btn_yes));
        main_btn_yes.setOnClickListener(v -> {
            // Проверка промокода и активация.
            loadingFragment.show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    loadingFragment.hide();
                }
            }, 350L);
        });

        viewGroup.setVisibility(View.GONE);
    }

    public void showPromoDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void hidePromoDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
