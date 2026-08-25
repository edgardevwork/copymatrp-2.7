package ru.edgar.nlremake.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.model.ProfileData;

public class ProfileDataAdapter extends RecyclerView.Adapter<ProfileDataAdapter.ViewHolder> {

    private final List<ProfileData> dataList;

    public ProfileDataAdapter(List<ProfileData> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_data_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileData currentItem = dataList.get(position);

        holder.tvStatLabel.setText(currentItem.getLabel());

        if (currentItem.isShowRubley()) {
            holder.imRub.setVisibility(View.VISIBLE);
            holder.tvStatValue.setText(currentItem.getValue());
        } else {
            holder.imRub.setVisibility(View.GONE);
            holder.tvStatValue.setText(currentItem.getValue());
        }

        if (currentItem.getValue().equals("Нет")) {
            holder.tvStatValue.setAlpha(0.4f);
        } else {
            holder.tvStatValue.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatLabel;
        TextView tvStatValue;
        ImageView imRub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatLabel = itemView.findViewById(R.id.tvStatLabel);
            tvStatValue = itemView.findViewById(R.id.tvStatValue);
            imRub = itemView.findViewById(R.id.ic_rub);
        }
    }
}
