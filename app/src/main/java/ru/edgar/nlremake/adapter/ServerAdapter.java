package ru.edgar.nlremake.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.model.Servers;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    Context context;
    ArrayList<Servers> slist;

    public ServerAdapter(Context context, ArrayList<Servers> slist) {
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
        Servers server = slist.get(position);
        holder.line.setBackgroundColor(Color.parseColor("#" + server.getColor()));
        holder.serverText.setText(server.getName());
    }

    @Override
    public int getItemCount() {
        return slist.size();
    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private View line;
        private TextView serverText;

        public ServerViewHolder(View view) {
            super(view);
            this.view = view;
            line = view.findViewById(R.id.line);
            serverText = view.findViewById(R.id.server_text);
        }
    }
}