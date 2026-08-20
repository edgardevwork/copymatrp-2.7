package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import javax.annotation.Nullable;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;

public class DialogFragment extends AppCompatActivity {

    private ViewGroup viewGroup;
    public CheckBox checkBox, checkBox2;

    public DialogFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        // === ЗАЩИТА ОТ СКВОЗНЫХ КЛИКОВ ===
        // Говорим системе, что этот слой сам поглощает все нажатия и не пускает их вниз
        viewGroup.setClickable(true);
        viewGroup.setFocusable(true);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Оставляем пустым. Касание фона просто поглощается и не идет дальше
            }
        });

        checkBox = viewGroup.findViewById(R.id.checkBox);
        checkBox2 = viewGroup.findViewById(R.id.checkBox2);

        viewGroup.setVisibility(View.GONE);
    }

    void showDialog(String name, String dname, String b1, String b2, View.OnClickListener click1, View.OnClickListener click2) {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);

        checkBox.setVisibility(View.GONE);
        checkBox2.setVisibility(View.GONE);

        TextView text = viewGroup.findViewById(R.id.bigText);
        text.setText(name);
        TextView text2 = viewGroup.findViewById(R.id.littleText);
        if(dname != null) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(dname);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text2.getLayoutParams();

            int marginRightInPixels = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._18sdp);

            params.rightMargin = marginRightInPixels;

            text2.setLayoutParams(params);
        } else {
            text2.setVisibility(View.GONE);
        }
        TextView text3 = viewGroup.findViewById(R.id.b1);
        if(b1 != null) {
            text3.setVisibility(View.VISIBLE);
            text3.setText(b1);
        } else {
            text3.setVisibility(View.GONE);
        }
        TextView text34 = viewGroup.findViewById(R.id.b2);
        if(b2 != null) {
            text34.setVisibility(View.VISIBLE);
            text34.setText(b2);
        } else {
            text34.setVisibility(View.GONE);
        }
        if(click1 != null) {
            FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
            main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
            main_btn_yes.setOnClickListener(click1);
            main_btn_yes.setVisibility(View.VISIBLE);
        } else {
            FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
            main_btn_yes.setVisibility(View.GONE);
        }
        if(click2 != null) {
            FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
            main_btn_no.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_no));
            main_btn_no.setOnClickListener(click2);
            main_btn_no.setVisibility(View.VISIBLE);
        } else {
            FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
            main_btn_no.setVisibility(View.GONE);
        }
    }

    void showErrorDialog(String name, String dname, String b1, View.OnClickListener click1, boolean isCheckBox, String dCheckBox) {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);

        checkBox.setChecked(false);
        checkBox2.setVisibility(View.GONE);

        if(isCheckBox) {
            checkBox.setText(dCheckBox);
            checkBox.setVisibility(View.VISIBLE);
        } else
            checkBox.setVisibility(View.GONE);

        TextView text = viewGroup.findViewById(R.id.bigText);
        text.setText(name);
        TextView text2 = viewGroup.findViewById(R.id.littleText);
        if(dname != null) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(dname);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text2.getLayoutParams();

            int marginRightInPixels = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._18sdp);

            params.rightMargin = marginRightInPixels;

            text2.setLayoutParams(params);

        } else {
            text2.setVisibility(View.GONE);
        }
        TextView text3 = viewGroup.findViewById(R.id.b1);
        if(b1 != null) {
            text3.setVisibility(View.VISIBLE);
            text3.setText(b1);
        } else {
            text3.setVisibility(View.GONE);
        }
        TextView text34 = viewGroup.findViewById(R.id.b2);
        text34.setVisibility(View.GONE);
        if(click1 != null) {
            FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
            main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
            main_btn_yes.setOnClickListener(click1);
            main_btn_yes.setVisibility(View.VISIBLE);
        } else {
            FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
            main_btn_yes.setVisibility(View.GONE);
        }
        FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
        main_btn_no.setVisibility(View.GONE);
    }

    void showDialogCheckBoxes(String name, String dname, String b1, View.OnClickListener click1, CheckBox[] checkBoxes) {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);

        checkBox.setChecked(checkBoxes[0].isChecked());
        checkBox2.setChecked(checkBoxes[1].isChecked());
        checkBox.setVisibility(View.VISIBLE);
        checkBox2.setVisibility(View.VISIBLE);

        StringBuilder sb1 = new StringBuilder();
        String str1 = String.format("Я принимаю <a href=\"https://crmp.pro\">%s</a>", "Пользовательское соглашение (EULA)");
        sb1.append(str1);

        CharSequence content1 = removeUnderlines(Html.fromHtml(sb1.toString()));

        checkBox.setText(content1);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setLinkTextColor(Color.parseColor("#b797e0"));// На старой матрешке также было )
        checkBox.setHighlightColor(0);

        StringBuilder sb2 = new StringBuilder();
        String str2 = String.format("Я принимаю <a href=\"https://crmp.pro\">%s</a>", "Политику конфиденциальности");
        sb2.append(str2);

        CharSequence content2 = removeUnderlines(Html.fromHtml(sb2.toString()));

        checkBox2.setText(content2);
        checkBox2.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox2.setLinkTextColor(Color.parseColor("#b797e0"));
        checkBox2.setHighlightColor(0);


        // === СЛУШАТЕЛЬ ДЛЯ ПЕРВОГО ЧЕКБОКСА ===
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxes[0].setChecked(isChecked);
            }
        });

        // === СЛУШАТЕЛЬ ДЛЯ ВТОРОГО ЧЕКБОКСА ===
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxes[1].setChecked(isChecked);
            }
        });

        TextView text = viewGroup.findViewById(R.id.bigText);
        text.setText(name);
        TextView text2 = viewGroup.findViewById(R.id.littleText);
        if(dname != null) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(dname);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text2.getLayoutParams();

            int marginRightInPixels = MainScreenActivity.getInstance().getResources().getDimensionPixelSize(R.dimen._44sdp);

            params.rightMargin = marginRightInPixels;

            text2.setLayoutParams(params);
        } else {
            text2.setVisibility(View.GONE);
        }
        TextView text3 = viewGroup.findViewById(R.id.b1);
        if(b1 != null) {
            text3.setVisibility(View.VISIBLE);
            text3.setText(b1);
        }
        if(click1 != null) {
            FrameLayout main_btn_yes = viewGroup.findViewById(R.id.main_btn_yes);
            main_btn_yes.setOnTouchListener(new InterfacesManager.animClickBtn(MainScreenActivity.getInstance(), main_btn_yes));
            main_btn_yes.setOnClickListener(click1);
            main_btn_yes.setVisibility(View.VISIBLE);
        }
        FrameLayout main_btn_no = viewGroup.findViewById(R.id.main_btn_no);
        main_btn_no.setVisibility(View.GONE);
    }

    private CharSequence removeUnderlines(CharSequence p_Text) {
        if (p_Text instanceof Spannable) {
            Spannable s = (Spannable) p_Text;
            // Находим все ссылки в тексте
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

            for (URLSpan span : spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);

                // Создаем новую ссылку поверх старой, но отключаем подчеркивание
                URLSpan newSpan = new URLSpan(span.getURL()) {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false); // Тот самый переключатель, который убирает линию
                    }
                };
                s.setSpan(newSpan, start, end, 0);
            }
            return s;
        }
        return p_Text;
    }

    void hideDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }

    /*public void show() {
        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        Point point = new Point();
        MainScreenActivity.getInstance().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();

    }*/
}
