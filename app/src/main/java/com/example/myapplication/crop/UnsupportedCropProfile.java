package com.example.myapplication.crop;

import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.SoilType;

public class UnsupportedCropProfile implements CropProfile {

    private final CropType crop;

    public UnsupportedCropProfile(CropType crop) {
        this.crop = crop;
    }

    @Override
    public CropType getCropType() {
        return crop;
    }

    @Override
    public int minSoilMoisture(SoilType soil) {
        return Integer.MAX_VALUE; // prevents irrigation rule
    }

    @Override
    public int maxSoilMoisture(SoilType soil) {
        return Integer.MAX_VALUE;
    }

    @Override
    public String irrigationAdvice(CropStage stage) {
        return "Irrigation advice not available for " + crop;
    }

    @Override
    public String fertilizerAdvice(CropStage stage, SoilType soil) {
        return "Fertilizer advice not available for " + crop;
    }

    @Override
    public String diseaseRisk(CropStage stage, float t, float h) {
        return "Disease risk advice not available for " + crop;
    }
}

