package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nvidia.devtech.NvEventQueueActivity;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.service.DownloadService;
import ru.edgar.space.SAMP;
import ru.edgar.space.UiManager;
import ru.edgar.space.core.ui.dialogs.Dialog;

public class AccountDialogFragment {

    private ViewGroup viewGroup;
    private Activity context;
    private LinearLayout btn_back, btn_delete, btn_exit;

    public AccountDialogFragment(Activity activity) {
        if(viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        context = activity;

        viewGroup = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog_account, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(activity, btn_back));
        btn_back.setOnClickListener(v -> {
            hideAccountDialog();
        });

        btn_delete = viewGroup.findViewById(R.id.btn_delete);
        btn_delete.setOnTouchListener(new UiManager.animClickBtn(activity, btn_delete));
        btn_delete.setOnClickListener(v -> {
            hideAccountDialog();
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showDialog("Точно хочешь удалить\nсвой аккаунт и все данные?",
                    "Твой аккаунт будет полностью удален через 7 дней, включая все личные данные" +
                            " и игровой прогресс.\nПосле этого востановление будет невозможно.", "Да", "Нет", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: Удаление аккаунта.
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogManager.hideDialog();
                        }
                    });
            dialogManager.changingButtonPriority(false);
        });

        btn_exit = viewGroup.findViewById(R.id.btn_exit);
        btn_exit.setOnTouchListener(new UiManager.animClickBtn(activity, btn_exit));
        btn_exit.setOnClickListener(v -> {
            // 1. Скрываем диалог
            hideAccountDialog();

            // 2. Сбрасываем авторизацию
            AppConfig.mAuth.signOut();
            AppConfig.isAuth = false;
            SAMP.getInstance().startGameFromButton();// FAKE LAUNCHER - без загрузки.
        });

        // === ЗАЩИТА ОТ СКВОЗНЫХ КЛИКОВ ===
        viewGroup.setClickable(true);
        viewGroup.setFocusable(true);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Оставляем пустым
            }
        });

        viewGroup.setVisibility(View.GONE);
    }

    public void showAccountDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void hideAccountDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}