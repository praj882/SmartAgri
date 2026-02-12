package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class CropDecisionActivity extends AppCompatActivity {

    TextView textViewDecision;
    Button buttonFetchDecision;
    OkHttpClient client = new OkHttpClient();

    // CHANGE THIS to false when deploying to production
    private final boolean USE_LOCAL_EMULATOR = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_decision);

        textViewDecision = findViewById(R.id.textViewDecision);
        buttonFetchDecision = findViewById(R.id.buttonFetchDecision);

        buttonFetchDecision.setOnClickListener(v -> fetchCropDecision());
    }

    private void fetchCropDecision() {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("crop", "wheat");
            jsonBody.put("soilMoisture", 30);
            jsonBody.put("temperature", 34);
            jsonBody.put("humidity", 45);
            jsonBody.put("rainExpected", false);
            jsonBody.put("growthStage", "vegetative");

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            // Use emulator HTTP for testing, HTTPS for production
            String url;
            if (USE_LOCAL_EMULATOR) {
                url = "http://10.0.2.2:5001/smartfarm-fa423/us-central1/getCropDecision";
            } else {
                url = "https://us-central1-smartfarm-fa423.cloudfunctions.net/getCropDecision";
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> textViewDecision.setText("Error: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String respStr = response.body().string();
                        runOnUiThread(() -> displayDecision(respStr));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            textViewDecision.setText("Exception: " + e.getMessage());
        }
    }

    private void displayDecision(String respStr) {
        try {
            JSONObject obj = new JSONObject(respStr);
            String decision = obj.getString("decision");
            String confidence = obj.getString("confidence");

            JSONArray reasonsArray = obj.getJSONArray("reasons");
            StringBuilder reasons = new StringBuilder();
            for (int i = 0; i < reasonsArray.length(); i++) {
                reasons.append("- ").append(reasonsArray.getString(i)).append("\n");
            }

            JSONObject action = obj.getJSONObject("action");
            String actionStr = "Water: " + action.getInt("waterLitersPerAcre") + " liters, Timing: " + action.getString("timing");

            String displayText = "Decision: " + decision + "\n"
                    + "Confidence: " + confidence + "\n"
                    + "Reasons:\n" + reasons.toString()
                    + "Action: " + actionStr;

            textViewDecision.setText(displayText);

        } catch (Exception e) {
            e.printStackTrace();
            textViewDecision.setText("Failed to parse response");
        }
    }
}