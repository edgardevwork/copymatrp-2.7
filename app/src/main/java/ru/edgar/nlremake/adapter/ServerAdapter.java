package ru.edgar.nlremake.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.matrp.R;
import ru.edgar.nlremake.activity.MainScreenActivity;
import ru.edgar.nlremake.data.PlayerData;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.loader.LauncherLoader;
import ru.edgar.nlremake.model.Server;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.nlremake.network.CrashReporter;
import ru.edgar.nlremake.network.FirebaseRepository;
import ru.edgar.nlremake.network.Interface;
import ru.edgar.space.EdgarConectV2;
import ru.edgar.space.UiManager;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    Context context;
    ArrayList<Server> slist;

    public ServerAdapter(Context context, ArrayList<Server> slist) {
        this.context = context;
        this.slist = slist;
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.server_item, parent, false);
        return new ServerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        Server server = slist.get(position);
        holder.line.setBackgroundColor(Color.parseColor("#" + server.getColor()));

        if (server.getPersonId() == -1 && !server.isRecommended()) {
            int load = server.getLoad();
            switch (load) {
                case 0:
                    holder.serverStatusText.setText("Низкая загрузка сервера");
                    holder.serverStatusText.setAlpha(0.4f);
                    holder.serverStatusText.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.serverStatusImage.setImageResource(R.drawable.ic_launcher_star);
                    break;
                case 1:
                    holder.serverStatusText.setText("Средняя загрузка сервера");
                    holder.serverStatusText.setAlpha(0.4f);
                    holder.serverStatusText.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.serverStatusImage.setImageResource(R.drawable.ic_launcher_star);
                    break;
                case 2:
                    holder.serverStatusText.setText("Высокая загрузка сервера");
                    holder.serverStatusText.setAlpha(0.4f);
                    holder.serverStatusText.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.serverStatusImage.setImageResource(R.drawable.ic_launcher_alert);
                    holder.serverStatusImage.setAlpha(0.4f);
                    break;
            }
        } else if (server.getPersonId() == -1) {
            holder.serverStatusText.setText("Рекомендуем");
            holder.serverStatusText.setAlpha(1.0f);
            holder.serverStatusText.setTextColor(Color.parseColor("#56B877"));
            holder.serverStatusImage.setImageResource(R.drawable.ic_star);
        } else {
            holder.serverStatusText.setText(server.getPersonName());
            holder.serverStatusText.setAlpha(1.0f);
            holder.serverStatusText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.serverStatusImage.setImageResource(R.drawable.ic_person);
        }

        if (server.getName().contains("#")) {
            String result = server.getName().substring(server.getName().indexOf("#") + 1).trim();
            holder.serverText.setText(result);
        } else {
            holder.serverText.setText(server.getName());
        }

        if ((server.isTest() && AppConfig.testApi) || !server.isEnterLock()) {
            holder.view.setOnTouchListener(new UiManager.animClickBtn(context, holder.view));
            holder.view.setOnClickListener(v -> {
                if (server.getPersonId() == -1) {
                    AppConfig.serverSelect = server;
                    UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                    DialogManager dialogManager = UiManager.getUiManager().getTyped(UiManager.DIALOG);
                    dialogManager.showCreateCharacterDialog();
                } else {
                    UiManager.getUiManager().getTyped(UiManager.LOADING).show();
                    FirebaseRepository.saveServerInfo(server.getId(), server.getName(), server.getColor(), server.getPersonName());

                    EdgarConectV2.host = server.getIp();
                    EdgarConectV2.port = server.getPort();
                    AppConfig.nickName = server.getPersonName();

                    Retrofit retrofitAccount = new Retrofit.Builder()
                            .baseUrl("https://google.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Interface accountInterface = retrofitAccount.create(Interface.class);

                    accountInterface.getAccountDetails(AppConfig.accountDetailsUrl, AppConfig.mAuth.getUid(),
                            AppConfig.serverId).enqueue(new Callback<PlayerData>() {

                        @Override
                        public void onResponse(Call<PlayerData> call, Response<PlayerData> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                PlayerData data = response.body();

                                // PlayerData
                                AppConfig.playerData = data;
                                //System.out.println(data.toString());

                                // ProfileData
                                AppConfig.profileData.clear();

                                if (data.getStatsList() != null) {
                                    AppConfig.profileData.addAll(data.getStatsList());
                                }

                                // Данные получены, продолжаем загрузку
                                UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                                UiManager.getUiManager().getTyped(UiManager.MENU).show();
                            } else {
                                DialogManager dialogManager =
                                        UiManager.getUiManager().getTyped(UiManager.DIALOG);

                                dialogManager.showErrorDialog(
                                        "Не удалось получить данные аккаунта!\nПовторите попытку позже.",
                                        null,
                                        "Ок",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (dialogManager.getIsChecked()) {
                                                    CrashReporter.sendBugReport(
                                                            context,
                                                            AppConfig.mAuth.getUid(),
                                                            "[ServerAdapter] getAccountDetails(..)...",
                                                            "if (response.isSuccessful()) {} else { ME }"
                                                    );
                                                }
                                                dialogManager.hideDialog();
                                                UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                                UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                                                UiManager.getUiManager().getTyped(UiManager.MENU).show();

                                            }
                                        },
                                        true,
                                        "Сообщить об ошибке"
                                );
                            }
                        }

                        @Override
                        public void onFailure(Call<PlayerData> call, Throwable t) {
                            DialogManager dialogManager =
                                    UiManager.getUiManager().getTyped(UiManager.DIALOG);

                            dialogManager.showErrorDialog(
                                    "Не удаётся получить данные аккаунта!\nПовторите попытку позже.",
                                    null,
                                    "Ок",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (dialogManager.getIsChecked()) {
                                                CrashReporter.sendBugReport(
                                                        context,
                                                        AppConfig.mAuth.getUid(),
                                                        "[ServerAdapter] getAccountDetails(..)...",
                                                        t.toString()
                                                );
                                            }
                                            dialogManager.hideDialog();

                                            UiManager.getUiManager().getTyped(UiManager.LOADING).hide();

                                            UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                                            UiManager.getUiManager().getTyped(UiManager.MENU).show();
                                        }
                                    },
                                    true,
                                    "Сообщить об ошибке"
                            );
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return slist.size();
    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private View line;
        private TextView serverText, serverStatusText;
        private ImageView serverStatusImage;

        public ServerViewHolder(View view) {
            super(view);
            this.view = view;
            line = view.findViewById(R.id.line);
            serverText = view.findViewById(R.id.server_text);
            serverStatusText = view.findViewById(R.id.server_status_text);
            serverStatusImage = view.findViewById(R.id.server_status_image);
        }
    }
}