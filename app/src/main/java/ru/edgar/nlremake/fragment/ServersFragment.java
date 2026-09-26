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

import ru.edgar.matrp.R;
import ru.edgar.nlremake.adapter.ServerAdapter;
import ru.edgar.nlremake.model.Servers;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.UiManager;

public class ServersFragment implements LauncherUiComponent {

    private ViewGroup viewGroup;
    private Activity context;
    private LinearLayout btn_back;
    private LinearLayout recommended, persons, all;
    private RecyclerView recommended_recycler, persons_recycler, all_recycler;

    @Override
    public void init(Activity activity) {
        if(viewGroup != null && !AppConfig.isStartGame) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_servers, (ViewGroup) null);
        UiManager.getUiManager().getFrontUI().addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);

        context = activity;

        recommended = activity.findViewById(R.id.recommended);
        persons = activity.findViewById(R.id.persons);
        all = activity.findViewById(R.id.all);
// 1. Создаем отдельный LayoutManager для рекомендованных серверов
        FlexboxLayoutManager layoutManager1 = new FlexboxLayoutManager(activity);
        layoutManager1.setFlexDirection(FlexDirection.ROW);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);

        recommended_recycler = activity.findViewById(R.id.recommended_recycler);
        recommended_recycler.setLayoutManager(layoutManager1); // Применяем первый

        ArrayList<Servers> servers = AppConfig.serverList;

        ServerAdapter serverAdapter = new ServerAdapter(activity, servers);
        recommended_recycler.setAdapter(serverAdapter);

        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(activity);
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);

        persons_recycler = activity.findViewById(R.id.persons_recycler);
        persons_recycler.setLayoutManager(layoutManager2);
        persons_recycler.setAdapter(serverAdapter);

        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(activity);
        layoutManager3.setFlexDirection(FlexDirection.ROW);
        layoutManager3.setFlexWrap(FlexWrap.WRAP);

        all_recycler = activity.findViewById(R.id.all_recycler);
        all_recycler.setLayoutManager(layoutManager3); // Применяем третий
        all_recycler.setAdapter(serverAdapter);

        btn_back = activity.findViewById(R.id.backButton);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(activity, btn_back));
        btn_back.setOnClickListener(v -> {
            hide();
            UiManager.getUiManager().getTyped(UiManager.MENU).show();
        });

        viewGroup.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
