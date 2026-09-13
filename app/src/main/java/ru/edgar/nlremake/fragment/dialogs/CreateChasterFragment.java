package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.fragment.MenuFragment;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.space.UiManager;

public class CreateChasterFragment {

    private ViewGroup viewGroup;
    private Activity context;
    private LinearLayout btn_back;

    public CreateChasterFragment(Activity activity) {
        if (viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        context = activity;

        viewGroup = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_create_character, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(context, btn_back));
        btn_back.setOnClickListener(v -> {
            hideCreateChasterDialog();
            MenuFragment menuFragment = UiManager.getUiManager().getTyped(UiManager.MENU);
            menuFragment.show();
        });

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

    public void showCreateChasterDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void hideCreateChasterDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
