package com.example.myapplication;

import android.util.Log;

import com.example.myapplication.crop.CropProfile;
import com.example.myapplication.crop.CropProfileRegistry;
import com.example.myapplication.model.AgroClimateData;
import com.example.myapplication.rules.ActionCategory;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.Decision;
import com.example.myapplication.rules.RuleInput;
import com.example.myapplication.rules.RuleResult;

public class DiseaseRules {

    public static void evaluate(RuleInput in, RuleResult result) {
        CropProfile crop = CropProfileRegistry.getOrDefault(in.crop);
        //CropProfile crop = CropProfileRegistry.get(in.crop);
        AgroClimateData ac = in.getClimateData();
        Log.d("TEMP", String.valueOf(ac.temperature));
        Log.d("HUMI", String.valueOf(ac.humidity));
        String risk = crop.diseaseRisk(
                in.stage,
                ac.temperature,
                ac.humidity
        );

        if (risk != null && !risk.isEmpty()) {
            result.addAction(
                    ActionCategory.PEST,
                    risk
            );
        }
    }
}



