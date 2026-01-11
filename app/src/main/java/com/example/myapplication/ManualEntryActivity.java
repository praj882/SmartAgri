package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.entity.FarmData;

public class ManualEntryActivity extends AppCompatActivity {

    EditText etMoisture, etTemp, etHumidity, etN, etP, etK;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        // Confirm correct activity + layout
        Toast.makeText(this, "Manual Entry Screen Opened", Toast.LENGTH_SHORT).show();

        // Bind views
        etMoisture = findViewById(R.id.etMoisture);
        etTemp = findViewById(R.id.etTemp);
        etHumidity = findViewById(R.id.etHumidity);
        etN = findViewById(R.id.etN);
        etP = findViewById(R.id.etP);
        etK = findViewById(R.id.etK);
        btnSave = findViewById(R.id.btnSave);

        // Save click
        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {

        // Validation
        if (etMoisture.getText().toString().trim().isEmpty() ||
                etTemp.getText().toString().trim().isEmpty() ||
                etHumidity.getText().toString().trim().isEmpty() ||
                etN.getText().toString().trim().isEmpty() ||
                etP.getText().toString().trim().isEmpty() ||
                etK.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FarmData data = new FarmData();

            data.moisture = Float.parseFloat(etMoisture.getText().toString().trim());
            data.temperature = Float.parseFloat(etTemp.getText().toString().trim());
            data.humidity = Float.parseFloat(etHumidity.getText().toString().trim());
            data.nitrogen = Float.parseFloat(etN.getText().toString().trim());
            data.phosphorus = Float.parseFloat(etP.getText().toString().trim());
            data.potassium = Float.parseFloat(etK.getText().toString().trim());

            data.timestamp = System.currentTimeMillis();
            data.source = "MANUAL";
            data.synced = false;

            // Save to Room DB
            AppDatabase.getInstance(this).farmDataDao().insert(data);

            Toast.makeText(this, "Data saved offline", Toast.LENGTH_SHORT).show();

            // Go back to Dashboard
            Intent intent = new Intent(ManualEntryActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
