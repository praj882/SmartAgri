package com.example.myapplication;

import com.example.myapplication.CropStageCalculator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus, tvSoil, tvTemp, tvHum, tvNPK, tvRecommendation,tvRecommendation1,tvResult,tvFertilizerResult,tvDiseaseRiskResult;
    Button btnRecommend,btnRecommend1,btnIrrigation,btnFertilizer,btnDiseaseRisk;
    EditText etCropName, etStartDate;
    DatabaseReference ref;
    FusedLocationProviderClient locationClient;
    VegetableAnalyzer VegAnalyzer;
    int lastSoil = 0, lastN = 0, lastP = 0, lastK = 0, lastTemp = 0, lasthum = 0;
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
        // CORRECT WAY
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://smartfarm-fa423-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );

        ref = database.getReference("farm1"); // CHANGE IF YOUR NODE IS DIFFERENT

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Integer soil = snapshot.child("soilMoisture").getValue(Integer.class);
                    Integer temp = snapshot.child("temperature").getValue(Integer.class);
                    Integer hum  = snapshot.child("humidity").getValue(Integer.class);
                    Integer n = snapshot.child("npk").child("N").getValue(Integer.class);
                    Integer p = snapshot.child("npk").child("P").getValue(Integer.class);
                    Integer k = snapshot.child("npk").child("K").getValue(Integer.class);

                    // Save last values for recommendation
                    lastSoil = soil != null ? soil : 0;
                    lastTemp = temp != null ? temp : 0;
                    lasthum = hum != null ? hum : 0;
                    lastN = n != null ? n : 0;
                    lastP = p != null ? p : 0;
                    lastK = k != null ? k : 0;

                    tvSoil.setText("Soil: " + lastSoil + "%");
                    tvTemp.setText("Temp: " + lastTemp);
                    tvHum.setText("Humidity: " + hum);
                    tvNPK.setText("N:" + lastN + " P:" + lastP + " K:" + lastK);

                    tvStatus.setText("Data updated successfully");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvStatus.setText("DB Error: " + error.getMessage());
            }
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
                    FertilizerAdvisor.getAdvice(crop,stage,soilType);

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
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("farm1/location");

                ref.child("latitude").setValue(lat);
                ref.child("longitude").setValue(lon);
            }
            String state = GetLocation.getAddressFromLocation(
                    MainActivity.this,
                    lat,
                    lon
            );
        });

    }

}
