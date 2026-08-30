package ru.edgar.nlremake.fragment.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.space.SAMP;

public class PromoDialogFragment {

    private ViewGroup viewGroup;

    public PromoDialogFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog_promo, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(viewGroup, -1, -1);
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

        viewGroup.setVisibility(View.GONE);
    }

    public void showDialog() {
        MainScreenActivity.getInstance().AnimVisibale(viewGroup, View.VISIBLE);
    }


}
