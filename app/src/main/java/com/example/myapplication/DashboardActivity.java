package com.example.myapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.DashboardViewModel;
import com.example.myapplication.data.local.FirebasePaths;
import com.example.myapplication.data.local.LocationSyncManager;
import com.example.myapplication.data.local.NetworkReceiver;
import com.example.myapplication.data.local.SessionManager;
import com.example.myapplication.data.local.SyncManager;
import com.example.myapplication.data.local.WeatherSyncManager;
import com.example.myapplication.data.local.entity.FarmData;
import com.example.myapplication.data.local.entity.NetworkMonitor;
import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.model.LocationData;
import com.example.myapplication.model.WeatherForecast;
import com.example.myapplication.network.weather.OpenWeatherResponse;
import com.example.myapplication.network.weather.WeatherApi;
import com.example.myapplication.network.weather.WeatherClient;
import com.example.myapplication.network.weather.WeatherMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.example.myapplication.model.SensorData;
import com.google.gson.Gson;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    // UI
    private TextView tvTempInline,tvHumidityInline,tvAddress, tvTemp, tvHumidity, tvMoisture, tvNpk, tvDeviceStatus, tvSource;
    private Button btnManual, btnHistory, mainActivity, cropDecision, adviceActivity, cropAdvisory;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference locationRef, sensorRef;

    // Network & Weather
    private NetworkMonitor networkMonitor;
    private WeatherApi weatherApi;
    private boolean weatherFetched = false;

    // State
    private double lat, lon;
    private String farmerId;
    private static final String DEVICE_ID = "device_001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        initLocation();
        initNetworkMonitor();

        farmerId = SessionManager.getFarmerId();
        if (farmerId == null) {
            Toast.makeText(this, "Farmer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViewModel();
        observeLocation();
        observeSensorData();

        SyncManager.sync(this);
        setupButtons();
    }

    /* -------------------- INIT -------------------- */

    private void initViews() {
        tvHumidityInline = findViewById(R.id.tvHumidityInline);
        tvTempInline = findViewById(R.id.tvTempInline);
        tvAddress = findViewById(R.id.tvAddress);
        tvDeviceStatus = findViewById(R.id.tvDeviceStatus);
        tvSource = findViewById(R.id.tvSource);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvMoisture = findViewById(R.id.tvMoisture);
        tvNpk = findViewById(R.id.tvNpk);

        btnManual = findViewById(R.id.btnManual);
        btnHistory = findViewById(R.id.btnHistory);
        mainActivity = findViewById(R.id.btnMainActivity);
        cropDecision = findViewById(R.id.btncropDecision);
        adviceActivity = findViewById(R.id.btnAdviceActivity);
        cropAdvisory = findViewById(R.id.btncropAdvisory);

        weatherApi = WeatherClient.getApi();
    }

    private void initLocation() {
        auth = FirebaseAuth.getInstance();
        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                LocationSyncManager.syncLocation(
                        this,
                        firebaseAuth.getCurrentUser().getUid(),
                        DEVICE_ID
                );
            }
        };
    }

    private void initNetworkMonitor() {
        networkMonitor = new NetworkMonitor(this, isOnline ->
                runOnUiThread(() -> {

                    tvDeviceStatus.setText(
                            isOnline ? "🌐 Internet: Online" : "🚫 Internet: Offline"
                    );

                    if (isOnline) {
                        // 🔥 Internet restored → fetch weather again
                        fetchWeather();
                    } else {
                        // ❄ Internet lost → reset UI safely
                        tvTempInline.setText("🌡 --°C");
                        tvHumidityInline.setText("💧 --%");
                    }
                })
        );
    }
    /* -------------------- VIEWMODEL -------------------- */

    private void setupViewModel() {
        DashboardViewModel viewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getDashboardData(farmerId, DEVICE_ID)
                .observe(this, this::updateUI);
    }

    /* -------------------- LOCATION -------------------- */

    private void observeLocation() {
        locationRef = FirebasePaths.location(farmerId, DEVICE_ID);
        locationRef.keepSynced(true);

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LocationData location = snapshot.getValue(LocationData.class);
                if (location == null) return;

                lat = location.latitude;
                lon = location.longitude;
                if (lat != 0 && lon != 0) {
                    resolveAddress(lat, lon);
                }
                if (!weatherFetched && lat != 0 && lon != 0) {
                    fetchWeather();
                    weatherFetched = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LOCATION", error.getMessage());
            }
        });
    }
    private void resolveAddress(double lat, double lon) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

                if (addresses == null || addresses.isEmpty()) {
                    Log.w("GEO", "No address found");
                    return;
                }

                Address address = addresses.get(0);

                String district = address.getSubAdminArea(); // District
                String state = address.getAdminArea();        // State
                String locationText = "";

                if (district != null) locationText += district;
                if (state != null) locationText += ", " + state;

                String finalLocationText = locationText;

                runOnUiThread(() ->
                        tvAddress.setText("📍 " + finalLocationText)
                );



            } catch (IOException e) {
                Log.e("GEO", "Geocoder failed", e);
            }
        });
    }
    /* -------------------- WEATHER -------------------- */

    private void fetchWeather() {

        WeatherSyncManager.syncWeather(
                lat,
                lon,
                weatherApi,
                new WeatherSyncManager.CallbackListener() {
                    @Override
                    public void onSuccess(WeatherForecast forecast) {

                        Log.d("DASH", "Weather = " + forecast);
                        float temperature = forecast.temperature;
                        float humidity = forecast.humidity;
                        tvTempInline.setText("🌡 " + temperature + "°C");
                        tvHumidityInline.setText("💧 " + humidity + "%");
                        // Inject into rule engine
                        //latestFarmData.forecast = forecast;
                    }
                    @Override
                    public void onFailure(String reason) {
                        Log.w("DASH", "Weather not available: " + reason);
                    }
                }
        );

    }

    /* -------------------- SENSOR DATA -------------------- */

    private void observeSensorData() {
        sensorRef = FirebasePaths.sensorData();

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SensorData data = snapshot.getValue(SensorData.class);
                if (data != null) {
                    saveToLocalDB(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        DashboardActivity.this,
                        "Sensor read failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    /* -------------------- UI -------------------- */

    private void updateUI(LatestFarmData data) {
        if (data == null) return;

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

    /* -------------------- DB -------------------- */

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

    private float[] parseNPK(String npk) {
        try {
            String[] p = npk.split("-");
            return new float[]{
                    Float.parseFloat(p[0]),
                    Float.parseFloat(p[1]),
                    Float.parseFloat(p[2])
            };
        } catch (Exception e) {
            return new float[]{0, 0, 0};
        }
    }

    /* -------------------- NAV -------------------- */

    private void setupButtons() {
        btnManual.setOnClickListener(v ->
                startActivity(new Intent(this, ManualEntryActivity.class)));
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        mainActivity.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        cropDecision.setOnClickListener(v ->
                startActivity(new Intent(this, CropDecisionActivity.class)));
        adviceActivity.setOnClickListener(v ->
                startActivity(new Intent(this, AdviceActivity.class)));
        cropAdvisory.setOnClickListener(v ->
                startActivity(new Intent(this, CropAdvisory.class)));
    }

    /* -------------------- LIFECYCLE -------------------- */

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        networkMonitor.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
        networkMonitor.unregister();
    }
}

