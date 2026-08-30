package ru.edgar.nlremake.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.adapter.ProfileDataAdapter;
import ru.edgar.nlremake.fragment.dialogs.PromoDialogFragment;
import ru.edgar.nlremake.model.ProfileData;
import ru.edgar.space.InterfacesManager;
import ru.edgar.space.SAMP;

public class ProfileFragment {

    private ViewGroup viewGroup;

    public ProfileFragment() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) SAMP.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_profile, (ViewGroup) null);
        SAMP.getInstance().getFrontUILayout().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        RecyclerView recyclerView = viewGroup.findViewById(R.id.profileStatsRecyclerView);

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

        ((LinearLayout)viewGroup.findViewById(R.id.btn_settings)).setOnTouchListener(new InterfacesManager.animClickBtn(SAMP.getInstance(), ((LinearLayout)viewGroup.findViewById(R.id.btn_settings))));
        ((LinearLayout)viewGroup.findViewById(R.id.btn_settings)).setOnClickListener(v -> {
            PromoDialogFragment u = new PromoDialogFragment();// ( Settings Dialog !!!!!!!!! ) old alpha 90
            u.showDialog();
        });

        viewGroup.setVisibility(View.GONE);

        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.VISIBLE);
    }
}
