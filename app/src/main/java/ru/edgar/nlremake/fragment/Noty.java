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

import androidx.viewbinding.ViewBinding;

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
    private ViewGroup oldViewGroup;
    public void show() {

        if(ittt != 0) {
            /*if(size != 0) {
                size = size + MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._40sdp);
            } else size = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._40sdp);*/
            oldViewGroup = viewGroup;
            animateViewSlideUpHide(oldViewGroup);
        }
        ittt++;
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.noty, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.bottomMargin = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._9sdp) + size;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.width = -2;
        layoutParams.height = -2;
        viewGroup.setLayoutParams(layoutParams);

        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setTranslationY(point.y);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setAlpha(0.0f);// если увед есть уже тип тот же то мы добавляем на открытие анимацию альфы
        viewGroup.animate()
                .translationY(0.0f)
                .setDuration(300L) /// 300
                .alpha(1.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Ждем 2 секунды (2000мс) ПОСЛЕ завершения анимации появления
                        // и безопасно запускаем скрытие в главном потоке
                        viewGroup.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //animateViewSlideUpHide(viewGroup);
                            }
                        }, 3000L);
                    }
                });
    }

    private void animateViewSlideUpHide(final View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }

        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.getAnimation().cancel();
        }
        view.clearAnimation();

        // Получаем текущие размеры и целевой отступ, который был при раскрытом состоянии
        final int measuredHeight = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        final int topMarginTarget = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp);

        // Общая дельта высоты, которую нужно убрать в 0
        final int totalHeightDelta = measuredHeight + topMarginTarget;

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();

                // При завершении анимации полностью обнуляем размеры и скрываем View
                if (interpolatedTime == 1.0f) {
                    lp.topMargin = 0;
                    lp.height = 0;
                    view.requestLayout();
                    view.setAlpha(0.0f);
                    view.setVisibility(View.GONE);
                    return;
                }

                // interpolatedTime идет от 0.0 до 1.0.
                // Для закрытия нам нужно обратное значение (от 1.0 до 0.0)
                float reverseTime = 1.0f - interpolatedTime;

                int currentDelta = (int) (totalHeightDelta * reverseTime);
                int calculatedMargin = Math.min(topMarginTarget, currentDelta);

                lp.topMargin = calculatedMargin;
                lp.height = currentDelta - calculatedMargin;
                view.setAlpha(reverseTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Фиксированная длительность 150 мс для всех типов View
        animation.setDuration(150L);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }

}
