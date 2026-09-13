package ru.edgar.space;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.database.annotations.NotNull;
import com.nvidia.devtech.NvEventQueueActivity;

import org.json.JSONObject;

import java.io.FileReader;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.fragment.LoadingFragment;
import ru.edgar.nlremake.fragment.MenuFragment;
import ru.edgar.nlremake.fragment.NotyManager;
import ru.edgar.nlremake.fragment.ProfileFragment;
import ru.edgar.nlremake.fragment.dialogs.CreateChasterFragment;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.model.Main;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.core.ui.chatedgar.ChatManager;
import ru.edgar.space.core.ui.dialogs.Dialog;
import ru.edgar.space.core.ui.hud.HudManager;
import ru.edgar.space.core.ui.hud.Speedometer;
import ru.edgar.space.core.ui.keyboard.KeyBoard;
import ru.edgar.space.core.ui.spawnmenu.SpawnMenu;
import ru.edgar.space.core.util.ConvertViewCoordsToGta;

public class UiManager {
    private static UiManager mUiManager = null;
    /*
    1 - Hud
    2 - Spedometr
    3 - Chat
    4 - Dialog
    5 - KeyBoard
    6 - SpawnMenu
    7 - LoaginMenu
    */

    private HudManager mHudManager = null;
    private Speedometer mSpeedometer = null;
    private ChatManager mChatManager = null;
    private Dialog mDialog = null;
    private KeyBoard mKeyBoard = null;
    private SpawnMenu mSpawnMenu = null;

    private LauncherUiComponent[] launcherUi;
    /*  Launcher UI
        1 - MenuFragment
        2 - ProfileFragment
        3 - NewsFragment
        4 - ServersFragment
        5 - DialogManager (AuthFragment, AuthEmailFragment, AccountDialogFragment,
        PromoDialogFragment, CreateChasterFragment, DialogFragment)
        6 - NotyManager
        7 - LoadingFragment
    */
    public static final int MENU = 0;
    public static final int PROFILE = 1;
    public static final int NEWS = 2;
    public static final int SERVERS = 3;
    public static final int DIALOG = 4;
    public static final int NOTY = 5;
    public static final int LOADING = 6;

    public UiManager(Activity activity) {
        mUiManager = this;
        launcherUi = new LauncherUiComponent[7];

        launcherUi[MENU] = new MenuFragment();
        launcherUi[PROFILE] = new ProfileFragment();
        //launcherUi[2] = new NewsFragment();
        //launcherUi[3] = new ServersFragment();
        launcherUi[DIALOG] = new DialogManager();
        launcherUi[NOTY] = new NotyManager();
        launcherUi[LOADING] = new LoadingFragment();

        // Вызываем инициализацию для каждого элемента
        for (LauncherUiComponent component : launcherUi) {
            if (component != null) {
                component.init(activity);
            }
        }

        if(AppConfig.isStartGame) {
            mHudManager = new HudManager(activity, 1);
            mSpeedometer = new Speedometer(activity, 2);
            mChatManager = new ChatManager(activity, 3);
            mDialog = new Dialog(activity, 4);
            mKeyBoard = new KeyBoard(activity, 5);
            mSpawnMenu = new SpawnMenu(activity, 6);
        }
    }
    public void cleanLauncherUi() {
        for (LauncherUiComponent component : launcherUi) {
            if (component != null) {
                component.hide();
            }
        }
    }

    public static UiManager getUiManager() {
        return mUiManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends LauncherUiComponent> T getTyped(int index) {
        // Проверка границ массива и null
        if (index < 0 || index >= launcherUi.length || launcherUi[index] == null) {
            return null;
        }

        // Получаем объект базового типа
        LauncherUiComponent component = launcherUi[index];

        // Приводим к нужному типу.
        // Если типы несовместимы, ClassCastException вылетит автоматически при возврате значения.
        return (T) component;
    }

    public LauncherUiComponent get(int index) {
        if (index <= 0 || index >= launcherUi.length || launcherUi[index] == null) {
            return null; // Или бросать исключение
        }
        return launcherUi[index];
    }

    @SuppressWarnings("unchecked")
    public <T extends LauncherUiComponent> T findByType(Class<T> clazz) {
        for (LauncherUiComponent component : launcherUi) {
            if (component != null && clazz.isInstance(component)) {
                return (T) component;
            }
        }
        return null;
    }
    // Использование: uiManager.findByType(DialogManager.class).showDialog();

    public void setRadarSize() {
        ConvertViewCoordsToGta.Data data = ConvertViewCoordsToGta.convertCoordsToGta(
                new ConvertViewCoordsToGta.Data(getHudManager().hud_map_bg.getX(),
                        getHudManager().hud_map_bg.getY(),
                        getHudManager().hud_map_bg.getWidth(),
                        getHudManager().hud_map_bg.getHeight()));
        SAMP.getInstance().SetRadarPos(data.getX(), data.getY(), data.getWidth(), data.getWidth());
        SAMP.getInstance().runOnUiThread(() -> {
            getHudManager().hud_map_bg.setVisibility(View.INVISIBLE);
        });
    }

    public void AnimVisibale(ViewGroup viewGroup, int targetState) {
        if (viewGroup != null) {
            if (targetState == View.VISIBLE) {
                // Сначала делаем видимым, потом анимируем
                viewGroup.setVisibility(View.VISIBLE);
                viewGroup.setAlpha(0.0f); // Начинаем с полной прозрачности

                viewGroup.animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(null); // Слушатель здесь больше не нужен для установки видимости
            } else {
                viewGroup.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                viewGroup.setVisibility(View.GONE);
                                super.onAnimationEnd(animation);
                            }
                        });
            }
        }
    }// Было 150

    public final static class animClickBtn implements View.OnTouchListener {

        public final AnimatorSet animatorSet;

        public final AnimatorSet animatorSet1;
        public final View view;

        public animClickBtn(Context context, View view) {
            this.view = view;
            view.setClickable(true);
            this.animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.reduce_size);
            this.animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.regain_size);
        }

        @Override // android.view.View.OnTouchListener
        @SuppressLint({"ClickableViewAccessibility"})
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            AnimatorSet animatorSet;
            int action = motionEvent.getAction() & 255;
            if (action == 0) {
                if (this.animatorSet1.isRunning()) {
                    this.animatorSet1.end();
                }
                this.animatorSet.setTarget(this.view);
                animatorSet = this.animatorSet;
            } else if (action != 1 && action != 3) {
                return false;
            } else {
                if (this.animatorSet.isRunning()) {
                    this.animatorSet.end();
                }
                this.animatorSet1.setTarget(this.view);
                animatorSet = this.animatorSet1;
            }
            animatorSet.start();
            return false;
        }
    }

    public void hideViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setVisibility(View.GONE);
            viewGroup.setAlpha(0.0f);
        }
    }

    public void showViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setVisibility(View.VISIBLE);
            viewGroup.setAlpha(1.0f);
        }
    }

    public void openingScreen(int i, @NotNull JSONObject json) {
        /*System.out.println("fczdikjnzdcolsfikuj");
        System.out.println(i);*/
        switch (i) {
            case 31: {
                SAMP.getInstance().runOnUiThread(() -> {
                    //UiManager.getUiManager().getDonateManager().show(json);
                });
                break;
            }
            case 32: {
                SAMP.getInstance().runOnUiThread(() -> {
                    UiManager.getUiManager().getSpawnMenu().ShowSpawnMenu();
                });
                break;
            }
        }
    }
    public void closingScreen(int i, @NotNull JSONObject json) {
        switch (i) {
            case 31: {
                SAMP.getInstance().runOnUiThread(() -> {
                    //UiManager.getUiManager().getDonateManager().hide(json);
                });
            }
        }
    }

    public FrameLayout getFrontUI() {
        System.out.println("bolean is start gamer - " + AppConfig.isStartGame);
        if(AppConfig.isStartGame) {
            return SAMP.getInstance().getFrontUILayout();
        } else {
            return MainScreenActivity.getInstance().getMainScreen();
        }
    }

    public HudManager getHudManager() {
        return mHudManager;
    }

    public Speedometer getSpeedometerManager() {
        return mSpeedometer;
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    public Dialog getDialogManager() {
        return mDialog;
    }

    public KeyBoard getKeyBoardManager() {
        return mKeyBoard;
    }

    public SpawnMenu getSpawnMenu() {
        return mSpawnMenu;
    }

}
