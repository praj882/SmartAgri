package com.example.myapplication;

import static com.example.myapplication.BuildConfig.OPEN_WEATHER_API_KEY;
import static com.example.myapplication.rules.CropStage.VEGETATIVE;
import static com.example.myapplication.rules.CropType.TOMATO;
import static com.example.myapplication.rules.RuleEngine.evaluate;
import static com.example.myapplication.rules.SoilType.CLAY;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.example.myapplication.rules.RuleResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdviceActivity extends AppCompatActivity {

    TextView tvIrrigation, tvFertilizer, tvPest;
    CardView cardIrrigation, cardFertilizer, cardPest;
    WeatherApi api = WeatherClient.getApi();
    WeatherForecast forecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        tvIrrigation = findViewById(R.id.tvIrrigationText);
        tvFertilizer = findViewById(R.id.tvFertilizerText);
        tvPest = findViewById(R.id.tvPestText);

        cardIrrigation = findViewById(R.id.cardIrrigation);
        cardFertilizer = findViewById(R.id.cardFertilizer);
        cardPest = findViewById(R.id.cardPest);
        String farmerId = SessionManager.getFarmerId();
        if (farmerId == null) {
            Toast.makeText(this, "Farmer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String deviceId = "device_001";
        DatabaseReference sensorRef = FirebasePaths.latestData(farmerId,deviceId);

        // 🔥 Always keep this data synced
        sensorRef.keepSynced(true);
        api.getCurrentWeather(
                28.6139,
                77.2090,
                BuildConfig.OPEN_WEATHER_API_KEY,
                "metric"
        ).enqueue(new Callback<OpenWeatherResponse>() {

            @Override
            public void onResponse(
                    Call<OpenWeatherResponse> call,
                    Response<OpenWeatherResponse> response
            ) {
                if (!response.isSuccessful() || response.body() == null) return;

                forecast =
                        WeatherMapper.toForecast(response.body());

                //data.forecast = forecast;   // 🔥 inject into rules
            }

            @Override
            public void onFailure(Call<OpenWeatherResponse> call, Throwable t) {
                // fallback → rules still work without weather
            }
        });
        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FB_FIX", "Advice onDataChange started");
                if (!snapshot.exists()) {
                    Log.w("ADVICE", "No sensor data");
                    return;
                }

                LatestFarmData data = snapshot.getValue(LatestFarmData.class);
                if (data == null) {
                    Log.e("ADVICE", "SensorData mapping failed");
                    return;
                }

                // 🔥 Rule engine ALWAYS local
                RuleResult result = evaluate(forecast,data, TOMATO, VEGETATIVE, CLAY);

                bindUI(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        AdviceActivity.this,
                        "Unable to read data",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void bindUI(RuleResult result) {

        cardIrrigation.setVisibility(View.GONE);
        cardFertilizer.setVisibility(View.GONE);
        cardPest.setVisibility(View.GONE);

        if (result == null) return;

        List<String> irrigation = result.getActions(ActionCategory.IRRIGATION);
        if (!irrigation.isEmpty()) {
            tvIrrigation.setText(joinLines(irrigation));
            cardIrrigation.setVisibility(View.VISIBLE);
        }

        List<String> fertilizer = result.getActions(ActionCategory.FERTILIZATION);
        if (!fertilizer.isEmpty()) {
            tvFertilizer.setText(joinLines(fertilizer));
            cardFertilizer.setVisibility(View.VISIBLE);
        }

        List<String> pest = result.getActions(ActionCategory.PEST);
        if (!pest.isEmpty()) {
            tvPest.setText(joinLines(pest));
            cardPest.setVisibility(View.VISIBLE);
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

