package com.example.myapplication.crop;

import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.SoilType;

public interface CropProfile {

    CropType getCropType();

    int minSoilMoisture(SoilType soil);

    int maxSoilMoisture(SoilType soil);

    String irrigationAdvice(CropStage stage);

    String fertilizerAdvice(CropStage stage, SoilType soil);

    String diseaseRisk(
            CropStage stage,
            float temperature,
            float humidity
    );
}

