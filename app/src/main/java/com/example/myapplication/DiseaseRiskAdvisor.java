package com.example.myapplication;

public class DiseaseRiskAdvisor {

    public static String getDiseaseRisk(
            String crop,
            String stage,
            float temperature,
            float humidity
    ) {

        crop = crop.toLowerCase();
        stage = stage.toLowerCase();

        // HIGH RISK
        if (humidity >= 80 && temperature >= 22 && temperature <= 30) {

            if (crop.equals("tomato")) {

                if (stage.equals("seedling")) {
                    return "HIGH RISK: Damping-off disease possible in tomato";
                }

                if (stage.equals("vegetative")) {
                    return "HIGH RISK: Leaf blight risk in tomato";
                }

                if (stage.equals("flowering")) {
                    return "HIGH RISK: Early blight may occur in tomato";
                }

                if (stage.equals("fruiting")) {
                    return "HIGH RISK: Fruit rot risk in tomato";
                }
            }

            if (crop.equals("chilli")) {

                if (stage.equals("seedling")) {
                    return "HIGH RISK: Root rot risk in chilli";
                }

                if (stage.equals("vegetative")) {
                    return "HIGH RISK: Leaf spot risk in chilli";
                }

                if (stage.equals("flowering")) {
                    return "HIGH RISK: Anthracnose risk in chilli";
                }

                if (stage.equals("fruiting")) {
                    return "HIGH RISK: Fruit rot risk in chilli";
                }
            }

            if (crop.equals("brinjal")) {

                if (stage.equals("seedling")) {
                    return "HIGH RISK: Wilt disease possible in brinjal";
                }

                if (stage.equals("vegetative")) {
                    return "HIGH RISK: Leaf spot risk in brinjal";
                }

                if (stage.equals("flowering")) {
                    return "HIGH RISK: Fungal infection risk in brinjal";
                }

                if (stage.equals("fruiting")) {
                    return "HIGH RISK: Fruit rot risk in brinjal";
                }
            }
        }

        // MEDIUM RISK
        if (humidity >= 65 && temperature >= 25 && temperature <= 35) {
            return "MEDIUM RISK: Weather favorable for disease, monitor crop";
        }

        // LOW RISK
        return "LOW RISK: Disease chances are low";
    }
}

