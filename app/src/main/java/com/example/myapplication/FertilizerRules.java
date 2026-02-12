package com.example.myapplication;

import com.example.myapplication.crop.CropProfile;
import com.example.myapplication.crop.CropProfileRegistry;
import com.example.myapplication.rules.ActionCategory;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.Decision;
import com.example.myapplication.rules.RuleInput;
import com.example.myapplication.rules.RuleResult;
import com.example.myapplication.rules.SoilType;

public class FertilizerRules {

    public static void evaluate(RuleInput in, RuleResult result) {
        CropProfile crop = CropProfileRegistry.getOrDefault(in.crop);
        //CropProfile crop = CropProfileRegistry.get(in.crop);

        String advice = crop.fertilizerAdvice(in.stage, in.soil);

        if (advice != null && !advice.isEmpty()) {
            result.addAction(
                    ActionCategory.FERTILIZATION,
                    advice
            );
        }
    }
}



