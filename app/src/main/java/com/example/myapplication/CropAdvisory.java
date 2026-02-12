package com.example.myapplication;
import com.example.myapplication.data.local.WeatherSyncManager;
import com.example.myapplication.model.LocationData;
import com.google.gson.Gson;
import com.example.myapplication.BuildConfig;
import static com.example.myapplication.rules.RuleEngine.evaluate;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.UI.CropTypeUI;
import com.example.myapplication.UI.SoilTypeUI;
import com.example.myapplication.crop.CropProfileRegistry;
import com.example.myapplication.data.local.FirebasePaths;
import com.example.myapplication.data.local.SessionManager;
import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.model.SensorData;
import com.example.myapplication.model.WeatherForecast;
import com.example.myapplication.network.weather.OpenWeatherResponse;
import com.example.myapplication.network.weather.WeatherApi;
import com.example.myapplication.network.weather.WeatherClient;
import com.example.myapplication.network.weather.WeatherMapper;
import com.example.myapplication.rules.ActionCategory;
import com.example.myapplication.rules.AlertType;
import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.RuleEngine;
import com.example.myapplication.rules.RuleResult;
import com.example.myapplication.rules.SoilType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropAdvisory extends AppCompatActivity {

    int lastSoil, lastN, lastP, lastK;
    float lastTemp, lasthum;

    double lat = 0.0, lon = 0.0;

    TextView tvResult, tvFertilizerResult, tvDiseaseRiskResult;
    Button btnIrrigation, btnFertilizer, btnDiseaseRisk;
    Spinner SpinnerSoilType, SpinnerCropType;
    EditText StartDate;

    private LatestFarmData data;
    private WeatherForecast forecast;
    private final WeatherApi weatherApi = WeatherClient.getApi();
    private boolean weatherFetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_advisory);

        initUI();

        String farmerId = SessionManager.getFarmerId();
        if (farmerId == null) {
            Toast.makeText(this, "Farmer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String deviceId = "device_001";

        observeLatestData(farmerId, deviceId);
        observeLocationAndWeather(farmerId, deviceId);

        btnIrrigation.setOnClickListener(v ->
                processRules(ActionCategory.IRRIGATION, tvResult));

        btnFertilizer.setOnClickListener(v ->
                processRules(ActionCategory.FERTILIZATION, tvFertilizerResult));

        btnDiseaseRisk.setOnClickListener(v ->
                processRules(ActionCategory.PEST, tvDiseaseRiskResult));
    }

    /* ---------------- INIT UI ---------------- */

    private void initUI() {

        SpinnerCropType = findViewById(R.id.spCropType);
        SpinnerCropType.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        CropTypeUI.values())
        );

        SpinnerSoilType = findViewById(R.id.spSoilType);
        SpinnerSoilType.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        SoilTypeUI.values())
        );

        StartDate = findViewById(R.id.etStartDate);

        btnIrrigation = findViewById(R.id.btnIrrigation);
        btnFertilizer = findViewById(R.id.btnFertilizer);
        btnDiseaseRisk = findViewById(R.id.btnDiseaseRisk);

        tvResult = findViewById(R.id.tvResult);
        tvFertilizerResult = findViewById(R.id.tvFertilizerResult);
        tvDiseaseRiskResult = findViewById(R.id.tvDiseaseRiskResult);
    }

    /* ---------------- FIREBASE ---------------- */

    private void observeLatestData(String farmerId, String deviceId) {
        DatabaseReference ref =
                FirebasePaths.latestData(farmerId, deviceId);

        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = snapshot.getValue(LatestFarmData.class);
                if (data == null) return;

                lastSoil = data.moisture;
                lastTemp = data.temperature;
                lasthum = data.humidity;
                lastN = data.nitrogen;
                lastP = data.phosphorus;
                lastK = data.potassium;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void observeLocationAndWeather(String farmerId, String deviceId) {
        DatabaseReference locRef =
                FirebasePaths.location(farmerId, deviceId);

        locRef.keepSynced(true);
        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LocationData loc = snapshot.getValue(LocationData.class);
                if (loc == null) return;

                lat = loc.latitude;
                lon = loc.longitude;

                if (!weatherFetched && lat != 0 && lon != 0) {
                    fetchWeather();
                    weatherFetched = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /* ---------------- WEATHER ---------------- */

    private void fetchWeather() {
        WeatherSyncManager.syncWeather(
                lat,
                lon,
                weatherApi,
                new WeatherSyncManager.CallbackListener() {

                    @Override
                    public void onSuccess(WeatherForecast weatherforecast) {
                        Log.d("DASH", "Weather = " + weatherforecast);
                        forecast = weatherforecast;
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

    /* ---------------- RULE ENGINE ---------------- */

    private void processRules(ActionCategory category, TextView outputView) {

        if (data == null) {
            outputView.setText("⏳ Waiting for sensor data...");
            return;
        }

        CropTypeUI cropUI =
                (CropTypeUI) SpinnerCropType.getSelectedItem();
        SoilTypeUI soilUI =
                (SoilTypeUI) SpinnerSoilType.getSelectedItem();
        String date = StartDate.getText().toString().trim();

        if (cropUI == CropTypeUI.SELECT_CROP ||
                soilUI == SoilTypeUI.SELECT_SOIL ||
                date.isEmpty()) {

            outputView.setText("Please select crop, soil type and date");
            return;
        }

        CropType crop = cropUI.toDomain();
        SoilType soil = soilUI.toDomain();
        // 🔐 UI-level guard (RIGHT PLACE)
        if (!CropProfileRegistry.isSupported(crop)) {
            outputView.setText(
                    "⚠️ Advice for " + crop + " is not available yet."
            );
            return;
        }

        String stageStr =
                CropStageCalculator.getStage(crop.name(), date);

        CropStage stage =
                CropStage.valueOf(stageStr.toUpperCase());

        RuleResult result = RuleEngine.evaluate(
                forecast,   // may be null → safe fallback
                data,
                crop,
                stage,
                soil
        );

        List<String> actions =
                result.getActions(category);

        outputView.setText(
                actions.isEmpty()
                        ? "No advice available"
                        : joinLines(actions)
        );
        //bindUI(result,category);
    }
    private void bindUI(RuleResult result,ActionCategory category) {

        //cardIrrigation.setVisibility(View.GONE);
        //cardFertilizer.setVisibility(View.GONE);
        //cardPest.setVisibility(View.GONE);

        if (result == null) return;
        tvResult.setText("");
        tvFertilizerResult.setText("");
        tvDiseaseRiskResult.setText("");

        List<String> irrigation = result.getActions(ActionCategory.IRRIGATION);
        if (!irrigation.isEmpty()) {
            tvResult.setText(joinLines(irrigation));
            //cardIrrigation.setVisibility(View.VISIBLE);
        }

        List<String> fertilizer = result.getActions(ActionCategory.FERTILIZATION);
        if (!fertilizer.isEmpty()) {
            tvFertilizerResult.setText(joinLines(fertilizer));
            //cardFertilizer.setVisibility(View.VISIBLE);
        }

        List<String> pest = result.getActions(ActionCategory.PEST);
        if (!pest.isEmpty()) {
            tvDiseaseRiskResult.setText(joinLines(pest));
            //cardPest.setVisibility(View.VISIBLE);
        }
    }
    private String joinLines(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append("• ").append(s).append("\n");
        }
        return sb.toString();
    }
}
