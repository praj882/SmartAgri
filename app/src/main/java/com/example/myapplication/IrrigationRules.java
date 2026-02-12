package com.example.myapplication;

import com.example.myapplication.crop.CropProfile;
import com.example.myapplication.crop.CropProfileRegistry;
import com.example.myapplication.model.AgroClimateData;
import com.example.myapplication.model.WeatherForecast;
import com.example.myapplication.rules.ActionCategory;
import com.example.myapplication.rules.Decision;
import com.example.myapplication.rules.RuleInput;
import com.example.myapplication.rules.RuleResult;
public class IrrigationRules {
    public static void evaluate(RuleInput in, RuleResult result) {
        CropProfile crop = CropProfileRegistry.getOrDefault(in.crop);
        //CropProfile crop = CropProfileRegistry.get(in.crop);

        int minMoisture = crop.minSoilMoisture(in.soil);
        int maxMoisture = crop.maxSoilMoisture(in.soil);
        int moisture = in.data.moisture;
        AgroClimateData ac = in.getClimateData();
        // 🌧️ RAIN OVERRIDE RULE
        if (ac != null && ac.rainExpected && ac.hoursAhead <= 24) {
            result.addAction(
                    ActionCategory.IRRIGATION,
                    "🌧️ Rain expected within 24 hours. Skip irrigation today."
            );
            return; // 🔥 IMPORTANT: stop irrigation rules
        }

        if (in.data.moisture < minMoisture) {

            String advice = crop.irrigationAdvice(in.stage);

            if (advice != null) {
                result.addAction(
                        ActionCategory.IRRIGATION,
                        advice
                );
            }
        }
        if (moisture > maxMoisture) {
            result.addAction(
                    ActionCategory.IRRIGATION,
                    "Soil moisture is high. Avoid irrigation."
            );
        }
    }
}


