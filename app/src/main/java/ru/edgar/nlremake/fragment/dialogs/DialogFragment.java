package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import javax.annotation.Nullable;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.InterfacesManager;

public class DialogFragment extends AppCompatActivity {

    private ViewGroup viewGroup;
    public CheckBox checkBox;

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

        checkBox = viewGroup.findViewById(R.id.checkBox);

        viewGroup.setVisibility(View.GONE);
    }

    void showDialog(String name, String dname, String b1, String b2, View.OnClickListener click1, View.OnClickListener click2) {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);

        checkBox.setVisibility(View.GONE);

        TextView text = viewGroup.findViewById(R.id.textView45);
        text.setText(name);
        TextView text2 = viewGroup.findViewById(R.id.dname);
        if(dname != null) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(dname);
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

        if(isCheckBox) {
            checkBox.setText(dCheckBox);
            checkBox.setVisibility(View.VISIBLE);
        } else
            checkBox.setVisibility(View.GONE);

        TextView text = viewGroup.findViewById(R.id.textView45);
        text.setText(name);
        TextView text2 = viewGroup.findViewById(R.id.dname);
        if(dname != null) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(dname);
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
