package com.example.myapplication;

public class FertilizerAdvisor {

    public static String getAdvice(
            String crop,
            String stage,
            String soilType) {

        crop = crop.toLowerCase();
        stage = stage.toLowerCase();
        soilType = soilType.toLowerCase();

        String advice = "";

        if (crop.equals("tomato")) {

            if (stage.equals("seedling"))
                advice = "Apply compost or organic manure";

            else if (stage.equals("vegetative"))
                advice = "Apply nitrogen-rich fertilizer";

            else if (stage.equals("flowering"))
                advice = "Apply phosphorus-rich fertilizer";

            else if (stage.equals("fruiting"))
                advice = "Apply potassium-rich fertilizer";
        }

        else if (crop.equals("brinjal")) {

            if (stage.equals("seedling"))
                advice = "Apply organic manure";

            else if (stage.equals("vegetative"))
                advice = "Apply nitrogen fertilizer";

            else if (stage.equals("flowering"))
                advice = "Apply phosphorus fertilizer";

            else if (stage.equals("fruiting"))
                advice = "Apply potassium fertilizer";
        }

        else if (crop.equals("chilli")) {

            if (stage.equals("seedling"))
                advice = "Apply compost or FYM";

            else if (stage.equals("vegetative"))
                advice = "Apply nitrogen fertilizer";

            else if (stage.equals("flowering"))
                advice = "Apply balanced NPK fertilizer";

            else if (stage.equals("fruiting"))
                advice = "Apply potassium fertilizer";
        }

        // Soil type adjustment
        if (!advice.isEmpty()) {
            if (soilType.equals("sandy"))
                advice += " (apply in smaller quantity, frequent)";
            else if (soilType.equals("clay"))
                advice += " (apply carefully, avoid excess)";
        }

        if (advice.isEmpty())
            advice = "No fertilizer advice available";

        return advice;
    }
}

