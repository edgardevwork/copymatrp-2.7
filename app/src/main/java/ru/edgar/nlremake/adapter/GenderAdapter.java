package ru.edgar.nlremake.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.model.Gender;
import ru.edgar.nlremake.network.AppConfig;
import ru.edgar.space.UiManager;

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.GenderViewHolder> {
    Context context;
    ArrayList<Gender> glist;
    public static int selectedPosition = RecyclerView.NO_POSITION;  // Храним позицию выделенного элемента

    public GenderAdapter(Context context, ArrayList<Gender> slist) {
        this.context = context;
        this.glist = slist;
        if (!slist.isEmpty()) { // Проверка на пустой список
            selectedPosition = 0;
        } else {
            selectedPosition = RecyclerView.NO_POSITION; // Если список пуст, не выделяем ничего
        }
    }

    @NonNull
    @Override
    public GenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.character_item, parent, false);
        return new GenderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GenderViewHolder holder, int position) {
        Gender gen = glist.get(position);
        Glide.with(context).load(AppConfig.skinsCDNUrl + gen.getSkin() + ".png?edgar=1829top09").into(holder.col);
        holder.view.setOnTouchListener(new UiManager.animClickBtn(context, holder.view));
        // Устанавливаем фон в зависимости от того, выделен ли элемент
        if (position == selectedPosition) {
            //SAMP.getInstance().setSkin(Integer.parseInt(slist.get(selectedPosition).getSkin()));
            holder.back.setImageResource(R.drawable.auth_bg_selected);
        } else {
            holder.back.setImageResource(R.drawable.auth_bg_skin);
        }

        holder.view.setOnClickListener(v -> {
            // 1. Сначала сбрасываем фон у предыдущего выделенного элемента
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition); // Обновляем предыдущий элемент
            }

            // 2. Устанавливаем новый выделенный элемент
            selectedPosition = holder.getAdapterPosition(); // Получаем правильную позицию
            notifyItemChanged(selectedPosition); // Обновляем текущий элемент

            // Дополнительно: можно выполнить какие-то действия с выделенным элементом
            // Например, передать данные в активность/фрагмент
            // genderSelectionListener.onGenderSelected(gen);
        });
    }

    @Override
    public int getItemCount() {
        return glist.size();
    }

    public static class GenderViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final ImageView col, back;

        public GenderViewHolder(View view) {
            super(view);
            this.view = view;
            this.back = (ImageView) view.findViewById(R.id.char_skin_bg);
            this.col = (ImageView) view.findViewById(R.id.char_skin_image);
        }
    }
}