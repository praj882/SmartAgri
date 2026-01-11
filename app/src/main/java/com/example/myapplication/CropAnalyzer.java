package com.example.myapplication;

public class CropAnalyzer {
    public static String getBestCrop(float moisture, float temperature, float nitrogen, float phosphorus, float potassium) {

        if (moisture < 20) {
            return "Millet / Sorghum (Dry crops recommended)";
        }

        if (nitrogen > 100 && phosphorus > 80 && potassium > 80 && temperature > 25) {
            return "Rice";
        }

        if (nitrogen > 60 && phosphorus > 40 && potassium > 40 && moisture > 30) {
            return "Wheat";
        }

        if (moisture > 40 && temperature > 20) {
            return "Sugarcane";
        }

        return "Generic Vegetables (Tomato / Brinjal / Chilli)";
    }
}
