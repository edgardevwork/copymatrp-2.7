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

import ru.edgar.matrp.R;
import ru.edgar.nlremake.fragment.dialogs.DialogManager;
import ru.edgar.nlremake.model.Server;
import ru.edgar.nlremake.network.AppConfig;
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
                    dialogManager.showCreateChesterDialog();
                } else {
                    HashMap<String, Object> Info = new HashMap<>();
                    Info.put("serverId", server.getId());
                    Info.put("serverName", server.getName());
                    Info.put("serverColor", server.getColor());
                    Info.put("personName", server.getPersonName());
                    FirebaseDatabase.getInstance().getReference().child("Users").child("User-server").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(Info);

                    EdgarConectV2.host = server.getIp();
                    EdgarConectV2.port = server.getPort();
                    AppConfig.nickName = server.getPersonName();
                    UiManager.getUiManager().getTyped(UiManager.SERVERS).hide();
                    UiManager.getUiManager().getTyped(UiManager.MENU).show();
                    // Получение данных и замена их в MenuFragment / Что-то типо updateUi
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