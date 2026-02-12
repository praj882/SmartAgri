package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.data.local.FirebasePaths;
import com.example.myapplication.data.local.SessionManager;
import com.example.myapplication.model.LatestFarmData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus, tvSoil, tvTemp, tvHum, tvNPK, tvRecommendation,tvRecommendation1,tvResult,tvFertilizerResult,tvDiseaseRiskResult;
    Button btnRecommend,btnRecommend1,btnIrrigation,btnFertilizer,btnDiseaseRisk;
    EditText etCropName, etStartDate;
    DatabaseReference ref;
    FusedLocationProviderClient locationClient;
    VegetableAnalyzer VegAnalyzer;
    int lastSoil = 0, lastN = 0, lastP = 0, lastK = 0;
    float lastTemp = 0, lasthum = 0;
    double lat =0.0;
    double lon =0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvSoil = findViewById(R.id.tvSoil);
        tvTemp = findViewById(R.id.tvTemp);
        tvHum = findViewById(R.id.tvHum);
        tvNPK = findViewById(R.id.tvNPK);
        tvRecommendation = findViewById(R.id.tvRecommendation);
        btnRecommend = findViewById(R.id.btnRecommend);
        tvRecommendation1 = findViewById(R.id.tvRecommendation1);
        btnRecommend1 = findViewById(R.id.btnRecommend1);
        etCropName = findViewById(R.id.etCropName);
        etStartDate = findViewById(R.id.etStartDate);
        btnIrrigation = findViewById(R.id.btnIrrigation);
        tvResult = findViewById(R.id.tvResult);
        btnFertilizer = findViewById(R.id.btnFertilizer);
        tvFertilizerResult = findViewById(R.id.tvFertilizerResult);
        btnDiseaseRisk = findViewById(R.id.btnDiseaseRisk);
        tvDiseaseRiskResult= findViewById(R.id.tvDiseaseRiskResult);
        Spinner spSoilType = findViewById(R.id.spSoilType);
        CropStageCalculator CropCal;
        String farmerId = SessionManager.getFarmerId();
        if (farmerId == null) {
            Toast.makeText(this, "Farmer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String deviceId = "device_001";
        // CORRECT WAY
        DatabaseReference ref =
                FirebasePaths.latestData(farmerId, deviceId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FB_FIX", "Main onDataChange started");
                if (!snapshot.exists()) {
                    tvStatus.setText("No data available");
                    return;
                }
                LatestFarmData data = snapshot.getValue(LatestFarmData.class);
                if (data == null) {
                    tvStatus.setText("Mapping failed");
                    return;
                }
                if (data != null) {
                    // Save for recommendation logic
                    lastSoil = data.moisture;
                    lastTemp = data.temperature;
                    lasthum = data.humidity;
                    lastN = data.nitrogen;
                    lastP = data.phosphorus;
                    lastK = data.potassium;

                    // UI update
                    tvSoil.setText("Soil: " + lastSoil + "%");
                    tvTemp.setText("Temp: " + lastTemp + "°C");
                    tvHum.setText("Humidity: " + lasthum + "%");
                    tvNPK.setText(
                            "N:" + lastN +
                                    " P:" + lastP +
                                    " K:" + lastK
                    );

                    tvStatus.setText("Latest data updated (" + data.source + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        btnRecommend.setOnClickListener(v -> {

            String rec = CropAnalyzer.getBestCrop(
                    lastSoil, lastN, lastP, lastK, lastTemp
            );

            tvRecommendation.setText("Recommendation: " + rec);
        });
        btnRecommend1.setOnClickListener(v -> {
            String loc = "0";//GetLocation.getAddressFromLocation(lat,lon);
            String rec = VegetableAnalyzer.recommendCrop(
                    lastSoil, lastTemp,lasthum,lastN
            );

            tvRecommendation1.setText(
                    "Location: " + loc +
                    "\n\nRecommendation:\n " + rec);
        });
        btnIrrigation.setOnClickListener(v -> {

            String crop = etCropName.getText().toString().trim();
            String date = etStartDate.getText().toString().trim();

            if (crop.isEmpty() || date.isEmpty()) {
                tvResult.setText("Please enter all inputs");
                return;
            }
            String soilType = spSoilType.getSelectedItem().toString();
            String stage = CropStageCalculator.getStage(crop, date);

            String irrigation =
                    IrrigationAdvisor.getAdvice(stage, lastSoil, lastTemp,soilType);

            tvResult.setText(
                    "Crop Stage: " + stage +
                            "\nSoil Moisture: " + lastSoil + "%" +
                            "\nTemperature: " + lastTemp + "°C" +
                            "\n\nIrrigation Advice:\n" + irrigation
            );
        });

        btnFertilizer.setOnClickListener(v -> {

            String crop = etCropName.getText().toString().trim();
            String date = etStartDate.getText().toString().trim();

            if (crop.isEmpty() || date.isEmpty()) {
                tvFertilizerResult.setText("Please enter all inputs");
                return;
            }
            String soilType = spSoilType.getSelectedItem().toString();
            String stage = CropStageCalculator.getStage(crop, date);

            String fertilizer =
                    FertilizerAdvisor .getAdvice(crop,stage,soilType);

            tvFertilizerResult.setText(
                    "Crop Stage: " + stage +
                            "\n\nfertilizer Advice:\n" + fertilizer
            );
        });

        btnDiseaseRisk.setOnClickListener(v -> {

            String crop = etCropName.getText().toString().trim();
            String date = etStartDate.getText().toString().trim();

            if (crop.isEmpty() || date.isEmpty()) {
                tvDiseaseRiskResult.setText("Please enter all inputs");
                return;
            }
            String soilType = spSoilType.getSelectedItem().toString();
            String stage = CropStageCalculator.getStage(crop, date);

            String DiseaseRisk =
                    DiseaseRiskAdvisor.getDiseaseRisk(crop,stage,lastTemp,lasthum);

            tvDiseaseRiskResult.setText(
                    "Crop Stage: " + stage +
                            "\nTemperature: " + lastTemp + "%" +
                            "\nHumidity: " + lasthum + "%" +
                            "\n\nDiseaseRisk Alert:\n" + DiseaseRisk
            );
        });
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                // Send to Firebase
                DatabaseReference locRef = FirebaseDatabase.getInstance()
                        .getReference("farmers")
                        .child(farmerId)
                        .child("devices")
                        .child(deviceId)
                        .child("location");

                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("latitude", lat);
                locationMap.put("longitude", lon);
                locationMap.put("updatedAt", System.currentTimeMillis());

                locRef.setValue(locationMap)
                        .addOnSuccessListener(aVoid ->
                                Log.d("FIREBASE", "Location saved successfully"))
                        .addOnFailureListener(e ->
                                Log.e("FIREBASE", "Location save failed", e));
            }
            String state = GetLocation.getAddressFromLocation(
                    MainActivity.this,
                    lat,
                    lon
            );
        });

    }

}
