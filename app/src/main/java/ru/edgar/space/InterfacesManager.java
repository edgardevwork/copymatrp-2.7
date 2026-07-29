package ru.edgar.space;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.annotations.NotNull;
import com.nvidia.devtech.NvEventQueueActivity;

import org.json.JSONObject;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.fragment.MenuFragment;
import ru.edgar.space.core.ui.chatedgar.ChatManager;
import ru.edgar.space.core.ui.dialogs.Dialog;
import ru.edgar.space.core.ui.hud.HudManager;
import ru.edgar.space.core.ui.hud.Speedometer;
import ru.edgar.space.core.ui.keyboard.KeyBoard;
import ru.edgar.space.core.ui.spawnmenu.SpawnMenu;
import ru.edgar.space.core.util.ConvertViewCoordsToGta;

public class InterfacesManager {
    private static InterfacesManager mInterfacesManager = null;
    public NvEventQueueActivity nvEventQueueActivity = null;
    public ViewGroup[] viewGroup;

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
    private MenuFragment menuFragment = null;

    public InterfacesManager(NvEventQueueActivity nvEventQueueActivity) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        mInterfacesManager = this;
        viewGroup = new ViewGroup[256];

        mHudManager = new HudManager(nvEventQueueActivity, 1);
        mSpeedometer = new Speedometer(nvEventQueueActivity, 2);
        mChatManager = new ChatManager(nvEventQueueActivity, 3);
        mDialog = new Dialog(nvEventQueueActivity, 4);
        mKeyBoard = new KeyBoard(nvEventQueueActivity, 5);
        mSpawnMenu = new SpawnMenu(nvEventQueueActivity, 6);
        menuFragment = new MenuFragment(nvEventQueueActivity, 7);
    }

    public static InterfacesManager getInterfacesManager() {
        return mInterfacesManager;
    }

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

    public void AnimVisibale(ViewGroup viewGroup, int view) {
        if (viewGroup != null) {
            if (view == View.VISIBLE) {
                viewGroup.animate().setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        viewGroup.setVisibility(View.VISIBLE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(1.0f);
            }else {
                viewGroup.animate().setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        viewGroup.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0.0f);
            }
        }
    }

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
                    //InterfacesManager.getInterfacesManager().getDonateManager().show(json);
                });
                break;
            }
            case 32: {
                SAMP.getInstance().runOnUiThread(() -> {
                    InterfacesManager.getInterfacesManager().getSpawnMenu().ShowSpawnMenu();
                });
                break;
            }
        }
    }
    public void closingScreen(int i, @NotNull JSONObject json) {
        switch (i) {
            case 31: {
                SAMP.getInstance().runOnUiThread(() -> {
                    //InterfacesManager.getInterfacesManager().getDonateManager().hide(json);
                });
            }
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
