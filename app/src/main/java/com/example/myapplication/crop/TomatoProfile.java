package com.example.myapplication.crop;

import com.example.myapplication.rules.CropStage;
import com.example.myapplication.rules.CropType;
import com.example.myapplication.rules.SoilType;

public class TomatoProfile implements CropProfile {

    @Override
    public CropType getCropType() {
        return CropType.TOMATO;
    }

    @Override
    public int minSoilMoisture(SoilType soil) {
        switch (soil) {
            case SANDY: return 40;
            case CLAY:  return 25;
            default:    return 30;
        }
    }

    @Override
    public int maxSoilMoisture(SoilType soil) {
        return 80;
    }

    @Override
    public String irrigationAdvice(CropStage stage) {
        switch (stage) {
            case SEEDLING:   return "Light irrigation (10–15 min)";
            case VEGETATIVE: return "Moderate irrigation (20–30 min)";
            case FLOWERING:  return "Critical stage – irrigate today";
            case FRUITING:   return "Avoid moisture stress during fruiting";
            default:         return null;
        }
    }

    @Override
    public String fertilizerAdvice(CropStage stage, SoilType soil) {

        String base;

        switch (stage) {
            case SEEDLING:   base = "Apply compost or FYM"; break;
            case VEGETATIVE: base = "Apply nitrogen-rich fertilizer"; break;
            case FLOWERING:  base = "Apply phosphorus-rich fertilizer"; break;
            case FRUITING:   base = "Apply potassium-rich fertilizer"; break;
            default:         return null;
        }

        if (soil == SoilType.SANDY)
            base += " (small & frequent doses)";
        else if (soil == SoilType.CLAY)
            base += " (avoid excess)";

        return base;
    }

    @Override
    public String diseaseRisk(
            CropStage stage,
            float t,
            float h) {

        if (h >= 80 && t >= 22 && t <= 30) {
            switch (stage) {
                case SEEDLING:   return "High risk of damping-off";
                case VEGETATIVE: return "High risk of leaf blight";
                case FLOWERING:  return "High risk of early blight";
                case FRUITING:   return "High risk of fruit rot";
            }
        }

        if (h >= 65)
            return "Moderate disease risk – monitor crop";

        return null;
    }
}

