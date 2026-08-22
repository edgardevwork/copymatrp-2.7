package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;

public class Noty {

    public ViewGroup viewGroup;

    public Noty() {
        if(viewGroup != null) {
            return;
        }


        //viewGroup.setVisibility(View.GONE);
    }

    int size = 0;
    int ittt = 0;
    public void show() {

        if(ittt != 0) {
            if(size != 0) {
                size = size + MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._48sdp);
            } else size = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._48sdp);
        }
        ittt++;
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.noty, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.bottomMargin = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp) + size;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.width = -2;
        layoutParams.height = -2;
        viewGroup.setLayoutParams(layoutParams);

        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setTranslationY(point.y);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate()
                .translationY(0.0f)
                .setDuration(150L)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Ждем 2 секунды (2000мс) ПОСЛЕ завершения анимации появления
                        // и безопасно запускаем скрытие в главном потоке
                        viewGroup.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animateViewSlideUpHide(viewGroup);
                            }
                        }, 3000L);
                    }
                });
    }

    private void animateViewSlideUpHide(final View view) {
        if (view.getVisibility() != View.VISIBLE || view.getAnimation() != null) {
            return;
        }

        view.clearAnimation();

        boolean isTextView = view instanceof TextView;

        int measuredHeight;
        if (isTextView) {
            view.measure(-2, -2);
            measuredHeight = view.getMeasuredHeight();
        } else {
            //int fixedHeight = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._28sdp);
            measuredHeight = view.getMeasuredHeight();
        }

        final int topMarginTarget = isTextView
                ? MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._2sdp)
                : MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();

        final int startTopMargin = params.topMargin;
        final int totalDelta = measuredHeight + Math.abs(startTopMargin - topMarginTarget);

        params.height = measuredHeight;
        params.topMargin = 0;
        view.requestLayout();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();

                if (interpolatedTime == 1.0f) {
                    lp.height = 0;
                    lp.topMargin = topMarginTarget;
                    view.setVisibility(View.GONE);
                    view.requestLayout();
                    return;
                }

                int currentDelta = (int) (totalDelta * interpolatedTime);

                int calculatedMargin = (startTopMargin < topMarginTarget)
                        ? Math.min(topMarginTarget, startTopMargin + currentDelta)
                        : Math.max(topMarginTarget, startTopMargin - currentDelta);

                int visibleHeight = Math.max(0, measuredHeight - currentDelta);

                lp.topMargin = calculatedMargin;
                lp.height = visibleHeight;
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(isTextView ? 150L : 300L);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }
}
