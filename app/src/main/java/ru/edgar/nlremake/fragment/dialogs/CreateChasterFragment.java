package ru.edgar.nlremake.fragment.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.adapter.GenderAdapter;
import ru.edgar.nlremake.fragment.MenuFragment;
import ru.edgar.nlremake.model.Gender;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.space.UiManager;

public class CreateChasterFragment {

    private ViewGroup viewGroup;
    private Activity context;
    private LinearLayout btn_back, btn_male, btn_female;

    ArrayList<Gender> mGender = new ArrayList<>();

    private RecyclerView recycler;

    private GenderAdapter genderAdapter;

    int iSex = 0;

    private static final int[] maleSkins = {98, 97, 35, 71, 113};

    private static final int[] femaleSkins = {40, 63};

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

        recycler = (RecyclerView) viewGroup.findViewById(R.id.rec);

        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(context, btn_back));
        btn_back.setOnClickListener(v -> {
            hideCreateChasterDialog();
            MenuFragment menuFragment = UiManager.getUiManager().getTyped(UiManager.MENU);
            menuFragment.show();
        });

        ImageView imagemale = viewGroup.findViewById(R.id.imagemale);
        TextView textmale = viewGroup.findViewById(R.id.textmale);
        ImageView imagefemale = viewGroup.findViewById(R.id.imagefemale);
        TextView textfemale = viewGroup.findViewById(R.id.textfemale);

        btn_male = viewGroup.findViewById(R.id.btn_male);
        btn_male.setOnTouchListener(new UiManager.animClickBtn(context, btn_male));
        btn_male.setOnClickListener(v -> {
            mGender.clear();
            mGender.add(new Gender("98"));
            mGender.add(new Gender("97"));
            mGender.add(new Gender("35"));
            mGender.add(new Gender("71"));
            mGender.add(new Gender("113"));
            genderAdapter = new GenderAdapter(context, mGender);
            recycler.setAdapter(genderAdapter);
            iSex = 0;
            btn_male.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffffff")));
            btn_female.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8044414A")));
            imagemale.setColorFilter(Color.parseColor("#000000"));
            textmale.setTextColor(Color.parseColor("#000000"));
            imagefemale.setColorFilter(Color.parseColor("#97949d"));
            textfemale.setTextColor(Color.parseColor("#97949d"));
        });

        btn_female = viewGroup.findViewById(R.id.btn_female);
        btn_female.setOnTouchListener(new UiManager.animClickBtn(context, btn_female));
        btn_female.setOnClickListener(v -> {
            mGender.clear();
            mGender.add(new Gender("40"));
            mGender.add(new Gender("63"));
            iSex = 1;
            genderAdapter = new GenderAdapter(context, mGender);
            recycler.setAdapter(genderAdapter);
            btn_male.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8044414A")));
            btn_female.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffffff")));
            imagemale.setColorFilter(Color.parseColor("#97949d"));
            textmale.setTextColor(Color.parseColor("#97949d"));
            imagefemale.setColorFilter(Color.parseColor("#000000"));
            textfemale.setTextColor(Color.parseColor("#000000"));
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
        mGender.clear();
        mGender.add(new Gender("98"));
        mGender.add(new Gender("97"));
        mGender.add(new Gender("35"));
        mGender.add(new Gender("71"));
        mGender.add(new Gender("113"));
        genderAdapter = new GenderAdapter(context, mGender);
        recycler.setAdapter(genderAdapter);
    }

    public void hideCreateChasterDialog() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
