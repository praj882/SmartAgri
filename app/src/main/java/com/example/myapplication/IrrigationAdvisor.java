package com.example.myapplication;

public class IrrigationAdvisor {

    public static String getAdvice(
            String cropStage,
            int soilMoisture,
            float temperature,
            String soilType) {

        soilType = soilType.toLowerCase();

        int threshold;

        // Soil-type-based threshold
        if (soilType.equals("sandy")) {
            threshold = 40;
        } else if (soilType.equals("clay")) {
            threshold = 25;
        } else {
            threshold = 30; // loamy default
        }

        if (soilMoisture < threshold) {

            if (cropStage.equals("Seedling"))
                return "Light irrigation (10–15 min)";

            if (cropStage.equals("Vegetative"))
                return "Moderate irrigation (20–30 min)";

            if (cropStage.equals("Flowering"))
                return "Critical stage – irrigate today";

            if (cropStage.equals("Fruiting"))
                return "Irrigation required – avoid stress";

        } else {
            return "No irrigation required today";
        }

        if (temperature > 35)
            return "High temperature – monitor soil moisture";

        return "Monitor field condition";
    }
}

