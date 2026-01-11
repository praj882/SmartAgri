package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.local.entity.FarmData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FarmDataAdapter extends RecyclerView.Adapter<FarmDataAdapter.ViewHolder> {

    List<FarmData> list;
    public FarmDataAdapter(List<FarmData> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farm_data, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        FarmData d = list.get(i);

        String date = new SimpleDateFormat(
                "dd MMM yyyy HH:mm", Locale.getDefault())
                .format(new Date(d.timestamp));

        h.txtDate.setText(date);
        h.txtSource.setText("Source: " + d.source);

        h.txtValues.setText(
                "Moisture: " + d.moisture +
                        " | Temp: " + d.temperature +
                        " | Humidity: " + d.humidity +
                        "\nN: " + d.nitrogen +
                        " P: " + d.phosphorus +
                        " K: " + d.potassium
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtValues, txtSource;
        TextView tvSyncStatus;

        ViewHolder(View v) {
            super(v);
            txtDate = v.findViewById(R.id.txtDate);
            txtValues = v.findViewById(R.id.txtValues);
            txtSource = v.findViewById(R.id.txtSource);
            tvSyncStatus = itemView.findViewById(R.id.tvSyncStatus);
        }
    }
}

