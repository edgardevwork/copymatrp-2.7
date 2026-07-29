package ru.edgar.space.core.ui.hud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvidia.devtech.NvEventQueueActivity;

import java.util.Formatter;

import ru.edgar.space.InterfacesManager;
import ru.edgar.matrp.R;
import ru.edgar.space.SAMP;
import ru.edgar.space.core.views.CircularProgressBar;

public class Speedometer {
    public NvEventQueueActivity nvEventQueueActivity = null;
    public ViewGroup viewGroup = null;
    public TextView mCarHP;
    public FrameLayout mStrela;
    public FrameLayout mStrela2;
    public ImageView mEngine;
    public TextView mFuel;
    public ImageView mBG;
    public ImageView mLight;
    public TextView[] textViews;
    public ImageView mLock;
    public TextView mMileage;
    public TextView mSpeed;
    public ImageView turn_left_bg;
    public ImageView turn_right_bg;
    public CircularProgressBar mSpeedLine;
    public ImageView povv, povv2;
    static final int BUTTON_TURN_LEFT = 1;
    static final int BUTTON_TURN_RIGHT = 2;
    static final int BUTTON_TURN_ALL = 3;

    static final int TURN_SIGNAL_LEFT = 1;
    static final int TURN_SIGNAL_RIGHT = 2;
    static final int TURN_SIGNAL_ALL = 3;
    int turnlight_tick_sound_1;
    int turnlight_tick_sound_2;
    boolean turnlight_state = false;
    public int Pov, Pov2;

    native void sendClick(int clickId);

    public Speedometer(NvEventQueueActivity nvEventQueueActivity, int guiId) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
        textViews = new TextView[11];
        show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void show() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.speedometer, (ViewGroup) null);
        SAMP.getInstance().getBackUILayout().addView(viewGroup, -2, -2);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.gravity = 81;
        viewGroup.setLayoutParams(layoutParams);

        for (int i10 = 0; i10 < 11; i10++) {
            TextView[] textViewArr = textViews;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("speed_");
            stringBuilder.append(i10);
            textViewArr[i10] = (TextView) viewGroup.findViewById(nvEventQueueActivity.getResources().getIdentifier(stringBuilder.toString(), "id", nvEventQueueActivity.getPackageName()));
        }

        turnlight_tick_sound_1 = SAMP.getInstance().soundPool.load(SAMP.getInstance(), R.raw.turnlight_tick_1, 0);
        turnlight_tick_sound_2 = SAMP.getInstance().soundPool.load(SAMP.getInstance(), R.raw.turnlight_tick_2, 0);

        mSpeed = viewGroup.findViewById(R.id.speed_text);
        turn_left_bg = (ImageView) viewGroup.findViewById(R.id.turn_left_bg);
        turn_right_bg = (ImageView) viewGroup.findViewById(R.id.turn_right_bg);
        mStrela = viewGroup.findViewById(R.id.turn_left);
        mStrela2 = viewGroup.findViewById(R.id.turn_right);
        mFuel = viewGroup.findViewById(R.id.fuel_text);
        mCarHP = viewGroup.findViewById(R.id.hp_text);
        mMileage = viewGroup.findViewById(R.id.mileage);
        mSpeedLine = viewGroup.findViewById(R.id.speed_progress);
        mEngine = viewGroup.findViewById(R.id.in_engine);
        mLock = viewGroup.findViewById(R.id.in_key);
        povv = viewGroup.findViewById(R.id.in_left);
        povv2 = viewGroup.findViewById(R.id.in_right);
        mBG = viewGroup.findViewById(R.id.speed_bg);

        mBG.setOnClickListener( view -> {
            sendClick(BUTTON_TURN_ALL);
        });
        mStrela.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, mStrela));
        mStrela.setOnClickListener(view -> {
            sendClick(BUTTON_TURN_LEFT);
        });
        mStrela2.setOnTouchListener(new InterfacesManager.animClickBtn(nvEventQueueActivity, mStrela2));
        mStrela2.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
            sendClick(BUTTON_TURN_RIGHT);
        });
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }

    public void setTurnlight(int turnlight){
        switch (turnlight)
        {
            case TURN_SIGNAL_LEFT: {
                turn_left_bg.setImageResource(R.drawable.speed_turn_left_bg_active);
                SAMP.getInstance().soundPool.play(turnlight_tick_sound_2, 0.2f, 0.1f, 1, 0, 1.0f);
                break;
            }
            case TURN_SIGNAL_RIGHT: {
                turn_right_bg.setImageResource(R.drawable.speed_turn_right_bg_active);
                SAMP.getInstance().soundPool.play(turnlight_tick_sound_2, 0.1f, 0.2f, 1, 0, 1.0f);
                break;
            }
            case TURN_SIGNAL_ALL: {
                turn_left_bg.setImageResource(R.drawable.speed_turn_left_bg_active);
                turn_right_bg.setImageResource(R.drawable.speed_turn_right_bg_active);
                SAMP.getInstance().soundPool.play(turnlight_tick_sound_2, 0.1f, 0.2f, 1, 0, 1.0f);
                break;
            }
            default: {
                turn_left_bg.setImageResource(R.drawable.speed_turn_left_bg);
                turn_right_bg.setImageResource(R.drawable.speed_turn_right_bg);
                //SAMP.getInstance().soundPool.play(turnlight_tick_sound_1, 0.2f, 0.1f, 1, 0, 1.0f);
            }
        }
    }

    public void UpdateSpeedInfo(int speed, int fuel, int hp, int mileage, int engine, int light, int belt, int lock) {
        hp= (int) hp/10;
        mFuel.setText(new Formatter().format("%d", Integer.valueOf(fuel)).toString());
        mMileage.setText(new Formatter().format("%06d", Integer.valueOf(mileage)).toString());
        mCarHP.setText(new Formatter().format("%d%s", Integer.valueOf(hp), "%").toString());
        mSpeedLine.setValue(Math.max(0, Math.min(200, speed)) * (mSpeedLine.getEndValue() / 200));
        for (int i12 = 0; i12 < 11; i12++) {
            int abs = Math.abs(Math.max(0, Math.min(200, speed)) - (i12 * 20));
            if (abs > 20) {
                textViews[i12].setAlpha(0.3f);
                textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            } else {
                if (abs < 10) {
                    textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                textViews[i12].setAlpha(((1.0f - (abs / 20.0f)) * 0.7f) + 0.3f);
            }
        }
        //mSpeedLine.setProgressMax(1000); )float) ((int) speed)
       // mSpeedLine.setProgress(prog);
        mSpeed.setText(String.valueOf(speed));
        /*if(speed == 0)
            mSpeed.setAlpha((float) 0.5);
            mSpeed.setText("000");
            //mSpeed.setText("pososi");
            mSpeed.setTextColor(activity.getResources().getColor(R.color.black));
        if(speed > 0)
            mSpeed.setAlpha((float) 1.0);
            mSpeed.setText(String.valueOf(speed));
            mSpeed.setTextColor(activity.getResources().getColor(R.color.white));*/
        if(engine == 1)
            mEngine.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mEngine.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
        if(lock == 1)
            mLock.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mLock.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
    }

    public void ShowSpeed() {
        InterfacesManager.getInterfacesManager().showViewGroup(viewGroup);
    }

    public void HideSpeed() {
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }
}