package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;

public class Noty {

    private static final List<NotyInstance> activeNoties = new ArrayList<>();
    private static final int MAX_NOTIES = 4;

    private static class NotyInstance {
        int id;
        ViewGroup viewGroup;
        Runnable autoHideRunnable;
    }

    public static void show(Context context, int id, String text, int delaySeconds) { // TODO: сделать не статичным или хз - сделать кнопку!!!!!
        for (int i = 0; i < activeNoties.size(); i++) {
            if (activeNoties.get(i).id == id) {
                removeNoty(activeNoties.get(i), true);
                break;
            }
        }

        if (activeNoties.size() >= MAX_NOTIES) {
            removeNoty(activeNoties.get(0), true);
        }

        final MainScreenActivity activity = MainScreenActivity.getInstance();
        final ViewGroup mainScreen = activity.getMainScreen();
        final ViewGroup notyView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.noty, mainScreen, false);

        ImageView iconView = notyView.findViewById(R.id.noty_icon);
        TextView textView = notyView.findViewById(R.id.noty_text);

        int boundedId = Math.max(0, Math.min(id, 67));
        int resId = context.getResources().getIdentifier("ic_noty_picture_" + boundedId, "drawable", context.getPackageName());
        if (resId != 0 && iconView != null) iconView.setImageResource(resId);
        if (textView != null) textView.setText(text);

        final NotyInstance instance = new NotyInstance();
        instance.id = id;
        instance.viewGroup = notyView;

        notyView.setOnClickListener(v -> removeNoty(instance, true));

        mainScreen.addView(notyView, -1, -1);
        activeNoties.add(instance);

        updatePositions(context);
        animateShow(notyView, delaySeconds, instance);
    }

    private static void updatePositions(Context context) {
        int step = context.getResources().getDimensionPixelSize(R.dimen._40sdp);
        int baseMargin = context.getResources().getDimensionPixelSize(R.dimen._9sdp);

        for (int i = 0; i < activeNoties.size(); i++) {
            ViewGroup vg = activeNoties.get(i).viewGroup;
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) vg.getLayoutParams();
            int currentTargetMargin = baseMargin + (activeNoties.size() - 1 - i) * step;

            if (vg.getVisibility() == View.VISIBLE && lp.bottomMargin != currentTargetMargin && lp.bottomMargin != 0) {
                vg.animate().translationY(lp.bottomMargin - currentTargetMargin).setDuration(150L)
                        .withEndAction(() -> {
                            lp.bottomMargin = currentTargetMargin;
                            vg.setTranslationY(0);
                            vg.requestLayout();
                        });
            } else {
                lp.bottomMargin = currentTargetMargin;
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                lp.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                vg.setLayoutParams(lp);
            }
        }
    }

    private static void removeNoty(NotyInstance instance, boolean animate) {
        if (!activeNoties.contains(instance)) return;
        activeNoties.remove(instance);

        if (instance.autoHideRunnable != null) {
            instance.viewGroup.removeCallbacks(instance.autoHideRunnable);
        }

        if (animate) {
            animateViewSlideUpHide(instance.viewGroup, () -> {
                // ПРАВКА: Выносим удаление во view.post(), чтобы избежать краша drawChild
                instance.viewGroup.post(() -> {
                    ViewGroup parent = (ViewGroup) instance.viewGroup.getParent();
                    if (parent != null) parent.removeView(instance.viewGroup);
                    updatePositions(instance.viewGroup.getContext());
                });
            });
        } else {
            ViewGroup parent = (ViewGroup) instance.viewGroup.getParent();
            if (parent != null) parent.removeView(instance.viewGroup);
            updatePositions(instance.viewGroup.getContext());
        }
    }

    private static void animateShow(final ViewGroup view, int delaySeconds, final NotyInstance instance) {
        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        view.clearAnimation();
        view.setTranslationY(point.y);
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .translationY(0.0f)
                .alpha(1.0f)
                .setDuration(300L)// TODO: 150L или 300L ?????
                .withEndAction(() -> {
                    instance.autoHideRunnable = () -> removeNoty(instance, true);
                    view.postDelayed(instance.autoHideRunnable, delaySeconds * 1000L);
                });
    }

    private static void animateViewSlideUpHide(final View view, final Runnable onEnd) {
        if (view.getVisibility() != View.VISIBLE) {
            if (onEnd != null) onEnd.run();
            return;
        }

        view.clearAnimation();
        final int measuredHeight = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();
        final int topMarginTarget = view.getContext().getResources().getDimensionPixelSize(R.dimen._8sdp);
        final int totalHeightDelta = measuredHeight + topMarginTarget;

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();

                // ПРАВКА: Если lp стал null (объект успели удалить), прерываемся
                if (lp == null) return;

                if (interpolatedTime == 1.0f) {
                    lp.topMargin = 0;
                    lp.height = 0;
                    view.requestLayout();
                    view.setAlpha(0.0f);
                    view.setVisibility(View.GONE);
                    /*view.post(() -> {
                        MainScreenActivity.getInstance().getMainScreen().removeView(view);
                    });*/
                    // ПРАВКА: Колбэк теперь безопасно отработает после завершения кадра
                    if (onEnd != null) onEnd.run();
                    return;
                }
                float reverseTime = 1.0f - interpolatedTime;
                int currentDelta = (int) (totalHeightDelta * reverseTime);
                int calculatedMargin = Math.min(topMarginTarget, currentDelta);
                lp.topMargin = calculatedMargin;
                lp.height = currentDelta - calculatedMargin;
                view.setAlpha(reverseTime);
                view.requestLayout();
            }
            @Override
            public boolean willChangeBounds() { return true; }
        };
        animation.setDuration(300L); // TODO: 150L или 300L ?????
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }
}
