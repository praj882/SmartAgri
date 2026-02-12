package com.example.myapplication.crop;

import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.SoilType;

public class BrinjalProfile implements CropProfile {

    @Override
    public CropType getCropType() {
        return CropType.BRINJAL;
    }

    // ---------------- IRRIGATION ----------------

    @Override
    public int minSoilMoisture(SoilType soil) {
        switch (soil) {
            case SANDY:
                return 45;
            case LOAM:
                return 40;
            case CLAY:
                return 35;
            default:
                return 40;
        }
    }

    @Override
    public int maxSoilMoisture(SoilType soil) {
        switch (soil) {
            case SANDY:
                return 65;
            case LOAM:
                return 60;
            case CLAY:
                return 55;
            default:
                return 60;
        }
    }

    @Override
    public String irrigationAdvice(CropStage stage) {
        switch (stage) {
            case SEEDLING:
                return "Light irrigation daily to support early root growth";
            case VEGETATIVE:
                return "Irrigate every 2–3 days to maintain steady growth";
            case FLOWERING:
                return "Ensure adequate moisture; avoid water stress";
            case FRUITING:
                return "Maintain soil moisture; avoid over-irrigation";
            default:
                return "Maintain moderate soil moisture";
        }
    }

    // ---------------- FERTILIZER ----------------

    @Override
    public String fertilizerAdvice(CropStage stage, SoilType soil) {
        switch (stage) {
            case VEGETATIVE:
                return "Apply nitrogen-rich fertilizer (e.g. Urea)";
            case FLOWERING:
                return "Apply balanced NPK (e.g. 19-19-19)";
            case FRUITING:
                return "Apply potassium-rich fertilizer to improve fruit size";
            default:
                return "Apply well-decomposed FYM before planting";
        }
    }

    // ---------------- DISEASE ----------------

    @Override
    public String diseaseRisk(
            CropStage stage,
            float temperature,
            float humidity
    ) {

        if (humidity > 70 && temperature >= 25 && temperature <= 35) {
            return "High risk of wilt and leaf spot diseases. Ensure proper drainage and use fungicide if required.";
        }
        return "Low disease risk at current conditions";
    }
}
