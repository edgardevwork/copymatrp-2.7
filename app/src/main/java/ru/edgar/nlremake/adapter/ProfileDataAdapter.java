package ru.edgar.nlremake.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ru.edgar.matrp.R;
import ru.edgar.nlremake.data.ProfileData;

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

        holder.tvStatLabel.setText(currentItem.getCaption());
        holder.tvStatValue.setText(currentItem.getText());

        holder.tvStatValue.setAlpha(currentItem.getOpacity());
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatLabel;
        TextView tvStatValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatLabel = itemView.findViewById(R.id.tvStatLabel);
            tvStatValue = itemView.findViewById(R.id.tvStatValue);
        }
    }
}
