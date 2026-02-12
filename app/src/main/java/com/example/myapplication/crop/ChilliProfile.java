package com.example.myapplication.crop;

import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.SoilType;

public class ChilliProfile implements CropProfile {

    @Override
    public CropType getCropType() {
        return CropType.CHILLI;
    }

    // ---------------- IRRIGATION ----------------

    @Override
    public int minSoilMoisture(SoilType soil) {
        switch (soil) {
            case SANDY:
                return 40;
            case LOAM:
                return 35;
            case CLAY:
                return 30;
            default:
                return 35;
        }
    }

    @Override
    public int maxSoilMoisture(SoilType soil) {
        switch (soil) {
            case SANDY:
                return 60;
            case LOAM:
                return 55;
            case CLAY:
                return 50;
            default:
                return 55;
        }
    }

    @Override
    public String irrigationAdvice(CropStage stage) {
        switch (stage) {
            case SEEDLING:
                return "Light and frequent irrigation to establish plants";
            case VEGETATIVE:
                return "Irrigate every 3–4 days depending on soil moisture";
            case FLOWERING:
                return "Avoid water stress; maintain uniform moisture";
            case FRUITING:
                return "Moderate irrigation; excess water reduces pungency";
            default:
                return "Maintain optimum soil moisture";
        }
    }

    // ---------------- FERTILIZER ----------------

    @Override
    public String fertilizerAdvice(CropStage stage, SoilType soil) {
        switch (stage) {
            case VEGETATIVE:
                return "Apply nitrogen fertilizer to promote leaf growth";
            case FLOWERING:
                return "Apply balanced NPK to support flowering";
            case FRUITING:
                return "Apply potassium-rich fertilizer for better yield and quality";
            default:
                return "Apply compost or FYM during land preparation";
        }
    }

    // ---------------- DISEASE ----------------

    @Override
    public String diseaseRisk(
            CropStage stage,
            float temperature,
            float humidity
    ) {
        if (humidity > 75 && temperature >= 24 && temperature <= 32) {
            return "High risk of leaf curl and anthracnose. Monitor crop and apply preventive measures.";
        }
        return "Low disease risk at current weather conditions";
    }
}

