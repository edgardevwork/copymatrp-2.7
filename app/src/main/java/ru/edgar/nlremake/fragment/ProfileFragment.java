package ru.edgar.nlremake.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.adapter.ProfileDataAdapter;
import ru.edgar.nlremake.data.PlayerData;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.data.ProfileData;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;

public class ProfileFragment implements LauncherUiComponent {

    private ViewGroup viewGroup;
    private LinearLayout btn_settings, btn_back, btn_payment, btn_promo;
    private TextView tv_balance, tv_donate, player_level, nick_name, exp, maxExp;
    private ImageView nick_name_status;
    private ProgressBar progress;
    private RecyclerView recyclerView;
    private Activity context;

    @Override
    public void init(Activity activity) {
        if(viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_profile, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        context = activity;

        exp = viewGroup.findViewById(R.id.exp);
        maxExp = viewGroup.findViewById(R.id.max_exp);

        progress = viewGroup.findViewById(R.id.progress);

        tv_balance = viewGroup.findViewById(R.id.tv_balance);
        tv_donate = viewGroup.findViewById(R.id.tv_donate);
        player_level = viewGroup.findViewById(R.id.player_level);
        nick_name_status = viewGroup.findViewById(R.id.nick_name_status);

        nick_name = viewGroup.findViewById(R.id.nick_name_servers);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_settings = viewGroup.findViewById(R.id.btn_settings);
        btn_payment = viewGroup.findViewById(R.id.btn_payment);
        btn_promo = viewGroup.findViewById(R.id.btn_promo);
        recyclerView = viewGroup.findViewById(R.id.profileStatsRecyclerView);

        btn_back.setOnTouchListener(new UiManager.animClickBtn(activity, btn_back));
        btn_back.setOnClickListener(v -> {
            hide();
            UiManager.getUiManager().getTyped(UiManager.MENU).show();
        });

        btn_settings.setOnTouchListener(new UiManager.animClickBtn(activity, btn_settings));
        btn_settings.setOnClickListener(v -> {
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showAccountDialog();
        });

        btn_payment.setOnTouchListener(new UiManager.animClickBtn(activity, btn_payment));
        btn_payment.setOnClickListener(v -> {
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showDialog("Упс! Данная функиця\nвременно не доступна!", "Но это не повод переживать!\nВозможно уже в ближайщее время ее сделают рабочей :)", "Хорошо", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.hideDialog();
                }
            }, null);
        });

        btn_promo.setOnTouchListener(new UiManager.animClickBtn(activity, btn_promo));
        btn_promo.setOnClickListener(v -> {
            DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
            dialogManager.showPromoDialog();
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

    @Override
    public void show() {
        PlayerData playerData = AppConfig.playerData;
        progress.setMax(playerData.getMaxExp());
        progress.setProgress(playerData.getExp());
        exp.setText(String.valueOf(playerData.getExp()));
        maxExp.setText("/ " + playerData.getMaxExp());

        DecimalFormat formatter=new DecimalFormat();
        DecimalFormatSymbols symbols= DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s= formatter.format(playerData.getMoney());
        tv_balance.setText(s);
        s= formatter.format(playerData.getRub());
        tv_donate.setText(s);

        player_level.setText(String.valueOf(playerData.getLevel()));
        nick_name_status.setImageResource(playerData.isVip() ? R.drawable.ic_premium : R.drawable.ic_nopremium);
        //nick_name.setText(AppConfig.nickName);
        nick_name.setText(playerData.getNickname());

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);

        List<ProfileData> profileData = AppConfig.profileData;

        ProfileDataAdapter adapter = new ProfileDataAdapter(profileData);
        recyclerView.setAdapter(adapter);

        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
