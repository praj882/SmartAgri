package com.example.myapplication.rules;

import com.example.myapplication.DiseaseRules;
import com.example.myapplication.FertilizerRules;
import com.example.myapplication.IrrigationRules;
import com.example.myapplication.model.AgroClimateData;
import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.model.WeatherForecast;

/**
 * Core rule engine.
 * Executes offline on Android device.
 */
public class RuleEngine {

    public static RuleResult evaluate(
            WeatherForecast forecast,
            LatestFarmData data,
            CropType crop,
            CropStage stage,
            SoilType soil) {
        RuleResult result = new RuleResult();
        AgroClimateData climate =
                AgroClimateResolver.resolve(data, forecast);
        RuleInput input = new RuleInput(data,climate, crop, stage, soil);


        IrrigationRules.evaluate(input, result);
        FertilizerRules.evaluate(input, result);
        DiseaseRules.evaluate(input, result);

        if (result.getDecisions().isEmpty()) {
            result.addDecision(Decision.NORMAL_CONDITION);
            result.addAction(
                    ActionCategory.GENERAL,
                    "Crop condition is normal. Continue monitoring."
            );
        }

        return result;
    }
}




