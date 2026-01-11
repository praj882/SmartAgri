package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.DashboardViewModel;
import com.example.myapplication.data.local.NetworkReceiver;
import com.example.myapplication.data.local.SessionManager;
import com.example.myapplication.data.local.SyncManager;
import com.example.myapplication.data.local.entity.FarmData;
import com.example.myapplication.data.local.entity.NetworkMonitor;
import com.example.myapplication.model.LatestFarmData;
import com.google.firebase.database.*;
import com.example.myapplication.model.SensorData;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

public class DashboardActivity extends AppCompatActivity {

    Button btnManual;
    Button btnHistory, mainActivity;
    TextView tvTemp, tvHumidity, tvMoisture, tvNpk,tvDeviceStatus,tvSource;
    DatabaseReference sensorRef;
    ValueEventListener sensorListener;
    private NetworkMonitor networkMonitor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        tvDeviceStatus = findViewById(R.id.tvDeviceStatus);
        tvSource = findViewById(R.id.tvSource);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvMoisture = findViewById(R.id.tvMoisture);
        tvNpk = findViewById(R.id.tvNpk);
        btnManual = findViewById(R.id.btnManual);
        btnHistory = findViewById(R.id.btnHistory);
        mainActivity = findViewById(R.id.btnMainActivity);
        Log.d("FB_FIX", "Dashboard started");
        DashboardViewModel viewModel =
                new ViewModelProvider(this)
                        .get(DashboardViewModel.class);
        String farmerId = SessionManager.getFarmerId();
        if (farmerId == null) {
            Toast.makeText(this, "Farmer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String deviceId = "device_001";
        viewModel.getDashboardData(farmerId, deviceId)
                .observe(this, data -> {

                    if (data == null) {
                        //tvStatus.setText("No Data");
                        return;
                    }
                    updateUI(data);
                });
        sensorRef = FirebaseDatabase.getInstance(
                "https://smartfarm-fa423-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("sensorData");
        Log.d("PATH", sensorRef.toString());
        //sensorListener = new ValueEventListener() {
        sensorRef.addValueEventListener(new ValueEventListener() {
            //sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FB_FIX", "onDataChange started");
                Log.d("FB", "snapshotmekya hi: " + snapshot.exists());
                Log.d("FB", "Rawdatabolo: " + snapshot.getValue());
                if (!snapshot.exists()) return;
                SensorData data = snapshot.getValue(SensorData.class);
                if (data == null) {
                    Log.e("FB", "Mapping failed");
                    return;
                }
                saveToLocalDB(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this,
                        "Firebase read failed", Toast.LENGTH_SHORT).show();
            }
        });
        networkMonitor = new NetworkMonitor(this, isOnline -> {
            runOnUiThread(() -> {
                if (isOnline) {
                    tvDeviceStatus.setText("🌐 Internet: Online");
                } else {
                    tvDeviceStatus.setText("🚫 Internet: Offline");
                }
            });
        });

        //sensorRef.addValueEventListener(sensorListener);
        SyncManager.sync(this);
        btnManual.setOnClickListener(v ->
                startActivity(new Intent(this, ManualEntryActivity.class))
        );

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        mainActivity.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
    }
    @Override
    protected void onStart() {
        super.onStart();
        networkMonitor.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        networkMonitor.unregister();
    }
    private float[] parseNPK(String npk) {
        float[] values = new float[]{0, 0, 0};

        if (npk == null || npk.isEmpty()) {
            return values;
        }

        try {
            String[] parts = npk.split("-");
            values[0] = Float.parseFloat(parts[0]); // Nitrogen
            values[1] = Float.parseFloat(parts[1]); // Phosphorus
            values[2] = Float.parseFloat(parts[2]); // Potassium
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }
    private void updateUI(LatestFarmData data) {
        long diff = System.currentTimeMillis() - data.timestamp;
        /*if (diff < 5 * 60 * 1000) {
            tvDeviceStatus.setText("Device Online");
        } else {
            tvDeviceStatus.setText("Device Offline");
        }*/
        tvTemp.setText(data.temperature + " °C");
        tvHumidity.setText(data.humidity + " %");
        tvMoisture.setText(String.valueOf(data.moisture));

        tvNpk.setText(
                "N:" + data.nitrogen +
                        " P:" + data.phosphorus +
                        " K:" + data.potassium
        );
        tvSource.setText(data.source);

    }
    private void saveToLocalDB(SensorData data) {

        float[] npk = parseNPK(data.npk);

        FarmData farmData = new FarmData();
        farmData.temperature = data.temperature;
        farmData.humidity = data.humidity;
        farmData.moisture = data.moisture;

        farmData.nitrogen = npk[0];
        farmData.phosphorus = npk[1];
        farmData.potassium = npk[2];

        farmData.timestamp = System.currentTimeMillis();
        farmData.source = "SENSOR";
        farmData.synced = false;

        AppDatabase.getInstance(this)
                .farmDataDao()
                .insert(farmData);
    }
}
