package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vk.id.group.subscription.compose.util.UserImageTransformation;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;

public class AuthEmailFragment {

    private ViewGroup viewGroup;
    private int status_auth = 0; // 0 - email, 1 - passes, 2 - code;
    private TextView bigText, littleText;
    private FrameLayout btn_back, main_btn_yes, main_btn_no;
    private LinearLayout email_layout_email, email_layout_pass, email_layout_pass2, email_layout_code;
    private EditText email_layout_email_input, email_layout_pass_input, email_layout_pass2_input, email_layout_code_input;

    public AuthEmailFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_auth_email, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        bigText = viewGroup.findViewById(R.id.bigText);
        littleText = viewGroup.findViewById(R.id.littleText);

        email_layout_email = viewGroup.findViewById(R.id.email_layout_email);
        email_layout_pass = viewGroup.findViewById(R.id.email_layout_pass);
        email_layout_pass2 = viewGroup.findViewById(R.id.email_layout_pass2);
        email_layout_code = viewGroup.findViewById(R.id.email_layout_code);

        email_layout_email_input = viewGroup.findViewById(R.id.email_layout_email_input);
        email_layout_pass_input = viewGroup.findViewById(R.id.email_layout_pass_input);
        email_layout_pass2_input = viewGroup.findViewById(R.id.email_layout_pass2_input);
        email_layout_code_input = viewGroup.findViewById(R.id.email_layout_code_input);

        main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
        main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
        main_btn_yes.setOnClickListener(v -> {
            switch (status_auth) {
                case 0:
                    String textEmail = email_layout_email_input.getText().toString();
                    int index = textEmail.indexOf("@");
                    if (index != -1 && email_layout_email_input.getText().toString().length() > 4) {
                        email_layout_email_input.setEnabled(false);
                        email_layout_email_input.setFocusable(false);
                        email_layout_email_input.setFocusableInTouchMode(false);
                        //email_layout_email_input.setTextColor(872415231);
                        email_layout_email.setAlpha(0.5f);

                        animateViewSlideUp(email_layout_pass);
                        animateViewSlideUp(email_layout_pass2);
                    } else {
                        // Ошибка Неправильный формат почты.
                    }
                    break;
            }
        });

        main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
        main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
        main_btn_no.setOnClickListener(v -> {

        });

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), btn_back));
        btn_back.setOnClickListener(v -> {
            hideDialog();
            DialogManager.getDialogManager().showAuthDialog(false);
        });

        viewGroup.setVisibility(View.GONE);
    }

    private void animateViewSlideUp(final View view) {
        // Если вьюха уже видна, анимацию не запускаем
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        // Сбрасываем старую анимацию, если она идет прямо сейчас
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.getAnimation().cancel();
        }
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);

        int fixedHeight = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._28sdp);
        int parentWidth = ((View) view.getParent()).getWidth();
        view.measure(
                View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(fixedHeight, View.MeasureSpec.EXACTLY)
        );

        final int measuredHeight = view.getMeasuredHeight();
        final int topMarginTarget = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._8sdp);
        final int totalHeightDelta = measuredHeight + topMarginTarget;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = 0;
        params.height = 0;
        view.requestLayout();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();

                if (interpolatedTime == 1.0f) {
                    lp.topMargin = topMarginTarget;
                    // ИСПРАВЛЕНИЕ: Вместо жесткого R.dimen._28sdp ставим реальную измеренную высоту.
                    // Это убирает резкий рывок/скачок в самом конце анимации.
                    lp.height = measuredHeight;
                    view.requestLayout();
                    view.setVisibility(View.VISIBLE); // Исход false
                    return;
                }

                // Рассчитываем шаги для исходного значения false (без инверсии времени)
                int currentDelta = (int) (totalHeightDelta * interpolatedTime);
                int calculatedMargin = Math.min(topMarginTarget, currentDelta);

                lp.topMargin = calculatedMargin;
                lp.height = currentDelta - calculatedMargin;
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Настройки плавности и времени
        animation.setDuration(300L);
        animation.setInterpolator(new DecelerateInterpolator()); // Плавное замедление к концу
        view.startAnimation(animation);
    }

    void showAuthEmailDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
        email_layout_email.setVisibility(View.VISIBLE);
        email_layout_pass.setVisibility(View.GONE);
        email_layout_pass2.setVisibility(View.GONE);
        email_layout_code.setVisibility(View.GONE);
        bigText.setText("Авторизация по эл. почте");
        littleText.setText("Пожалуйста, введи свой адрес эл. почты, чтобы\nпродолжить процесс регистрации или авторизовать\nтвой аккаунт в игре.");
        returnToZero();
    }

    void returnToZero() {
        email_layout_email_input.setEnabled(true);
        email_layout_email_input.setFocusable(true);
        email_layout_email_input.setFocusableInTouchMode(true);
        email_layout_email_input.setTextColor(-1);
        email_layout_email_input.setText("");

        email_layout_pass_input.setEnabled(true);
        email_layout_pass_input.setFocusable(true);
        email_layout_pass_input.setFocusableInTouchMode(true);
        email_layout_pass_input.setTextColor(-1);
        email_layout_pass_input.setText("");

        email_layout_pass2_input.setEnabled(true);
        email_layout_pass2_input.setFocusable(true);
        email_layout_pass2_input.setFocusableInTouchMode(true);
        email_layout_pass2_input.setTextColor(-1);
        email_layout_pass2_input.setText("");

        email_layout_code_input.setEnabled(true);
        email_layout_code_input.setFocusable(true);
        email_layout_code_input.setFocusableInTouchMode(true);
        email_layout_code_input.setTextColor(-1);
        email_layout_code_input.setText("");

        status_auth = 0;
    }

    void hideDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }
}
