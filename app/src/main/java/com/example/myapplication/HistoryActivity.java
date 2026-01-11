package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.entity.FarmData;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FarmDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FarmData> dataList =
                AppDatabase.getInstance(this).farmDataDao().getAll();

        adapter = new FarmDataAdapter(dataList);
        recyclerView.setAdapter(adapter);

    }
}
