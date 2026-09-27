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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.adapter.ServerAdapter;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.model.Server;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.network.Interface;
import ru.edgar.nlremake.other.LauncherUiComponent;
import ru.edgar.space.SAMP;
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

        FlexboxLayoutManager layoutManager1 = new FlexboxLayoutManager(activity);
        layoutManager1.setFlexDirection(FlexDirection.ROW);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);

        recommended_recycler = activity.findViewById(R.id.recommended_recycler);
        recommended_recycler.setLayoutManager(layoutManager1); // Применяем первый

        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(activity);
        layoutManager2.setFlexDirection(FlexDirection.ROW);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);

        persons_recycler = activity.findViewById(R.id.persons_recycler);
        persons_recycler.setLayoutManager(layoutManager2);

        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(activity);
        layoutManager3.setFlexDirection(FlexDirection.ROW);
        layoutManager3.setFlexWrap(FlexWrap.WRAP);

        all_recycler = activity.findViewById(R.id.all_recycler);
        all_recycler.setLayoutManager(layoutManager3); // Применяем третий

        btn_back = activity.findViewById(R.id.backButton);
        btn_back.setOnTouchListener(new UiManager.animClickBtn(activity, btn_back));
        btn_back.setOnClickListener(v -> {
            if (AppConfig.nickName.isEmpty()) {
                DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
                dialogManager.showDialog("Вернуться к экрану входа?", "Если нажмешь эту кнопку, тебе прийдется войти снова.", "Отмена", "Да", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogManager.hideDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 1. Скрываем диалог
                        dialogManager.hideDialog();

                        // 2. Сбрасываем авторизацию
                        AppConfig.mAuth.signOut();
                        AppConfig.isAuth = false;
                        AppConfig.nickName = "";
                        AppConfig.serverId = -1;
                        UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                        SAMP.getInstance().startGameFromButton();// FAKE LAUNCHER - без загрузки.
                    }
                });
                dialogManager.changingButtonPriority(false);
                return;
            }
            hide();
            UiManager.getUiManager().getTyped(UiManager.MENU).show();
        });

        viewGroup.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        updateServers();
        UiManager.getUiManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void updateItems() {
        if (!AppConfig.serverList.isEmpty()) {
            int totalCount = AppConfig.serverList.size();
            ArrayList<Server> allServers = new ArrayList<>(totalCount);
            ArrayList<Server> personServers = new ArrayList<>(totalCount);
            ArrayList<Server> recommendedServers = new ArrayList<>(totalCount);

            // Распределяем сервера по категориям
            for (Server server : (ArrayList<Server>) AppConfig.serverList) {
                if (server.getPersonId() != -1) {
                    personServers.add(server);
                } else if (server.isRecommended()) {
                    recommendedServers.add(server);
                } else {
                    allServers.add(server);
                }
            }

            // Скорость х3: Отрисовка UI
            updateRecyclerVisibility(persons, persons_recycler, personServers);
            updateRecyclerVisibility(recommended, recommended_recycler, recommendedServers);
            updateRecyclerVisibility(all, all_recycler, allServers);
        }
    }

    public void updateServers() {
        if (AppConfig.isAuth) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://google.com/") // Рекомендуется вынести базовый URL в AppConfig
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Interface sInterface = retrofit.create(Interface.class);

            sInterface.getServers(AppConfig.serversUrl, FirebaseAuth.getInstance().getUid()).enqueue(new Callback<List<Server>>() {
                @Override
                public void onResponse(Call<List<Server>> call, Response<List<Server>> response) {
                    if (response.body() == null) return;

                    AppConfig.serverList.clear();
                    AppConfig.serverList.addAll(response.body());

                    updateItems();
                }

                @Override
                public void onFailure(Call<List<Server>> call, Throwable t) {
                    DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
                    dialogManager.showErrorDialog("Не удаётся установить соединение с сервером!\nПовторите попытку позже.", null, "Повторить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialogManager.getIsChecked()) {
                                CrashReporter.sendBugReport(
                                        context,
                                        AppConfig.mAuth.getUid(),
                                        "[ServersFragment] getServers(..)...",
                                        t.toString()
                                );
                            }
                            dialogManager.hideDialog();
                            updateServers();
                        }
                    }, true, "Сообщить об ошибке");
                }
            });
        }
    }

    // Вспомогательный метод для ускорения и чистоты кода
    private void updateRecyclerVisibility(LinearLayout container, RecyclerView recyclerView, ArrayList<Server> items) {
        if (!items.isEmpty()) {
            container.setVisibility(View.VISIBLE);
            ServerAdapter serverAdapter = new ServerAdapter(context, items);
            recyclerView.setAdapter(serverAdapter);
        } else {
            container.setVisibility(View.GONE);
        }
    }

    @Override
    public void hide() {
        UiManager.getUiManager().AnimVisibale(viewGroup, View.GONE);
    }
}
