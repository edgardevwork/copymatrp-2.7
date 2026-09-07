package ru.edgar.nlremake.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.adapter.ProfileDataAdapter;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.fragment.dialogs.PromoDialogFragment;
import ru.edgar.nlremake.model.ProfileData;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;
import ru.edgar.space.SAMP;

public class ProfileFragment implements LauncherUiComponent {

    private ViewGroup viewGroup;
    private LinearLayout btn_settings, btn_back, btn_payment, btn_promo;
    private RecyclerView recyclerView;

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

        DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);

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
            dialogManager.showAccountDialog();
        });

        btn_payment.setOnTouchListener(new UiManager.animClickBtn(activity, btn_payment));
        btn_payment.setOnClickListener(v -> {
            dialogManager.showDialog("Упс! Данная функиця\nвременно не доступна!", "Но это не повод переживать!\nВозможно уже в ближайщее время ее сделают рабочей :)", "Хорошо", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.hideDialog();
                }
            }, null);
        });

        btn_promo.setOnTouchListener(new UiManager.animClickBtn(activity, btn_promo));
        btn_promo.setOnClickListener(v -> {
            new PromoDialogFragment(activity);
        });

        viewGroup.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(SAMP.getInstance());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);

        List<ProfileData> profileData = new ArrayList<>();

        profileData.add(new ProfileData("Денег в банке", "4525", true));
        profileData.add(new ProfileData("Дом", "Нет", false));
        profileData.add(new ProfileData("Телефон", "2229070", false));
        profileData.add(new ProfileData("Семья", "Нет", false));
        profileData.add(new ProfileData("Фракция", "Нет", false));
        profileData.add(new ProfileData("Бизнес", "Нет", false));

        ProfileDataAdapter adapter = new ProfileDataAdapter(profileData);
        recyclerView.setAdapter(adapter);

        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
