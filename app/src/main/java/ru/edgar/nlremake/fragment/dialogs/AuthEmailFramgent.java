package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;

public class AuthEmailFramgent {
    private ViewGroup viewGroup;

    public AuthEmailFramgent() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainScreenActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_auth_email, (ViewGroup) null);
        MainScreenActivity.getInstance().getMainScreen().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        viewGroup.setVisibility(View.VISIBLE);
    }

    void showAuthEmail() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
    }

    void hideDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.GONE);
    }
}
