package com.example.myapplication;

import android.content.Context;

public class VegetableAnalyzer {
    private Context context;
    public VegetableAnalyzer(Context context) {
        this.context = context;
    }
    public static String recommendCrop(
            int soilMoisture,
            float temp,
            float humidity,
            float ph) {

        int tomato = 0, brinjal = 0, chilli = 0, okra = 0;

        // TOMATO
        if (temp >= 18 && temp <= 30) tomato += 2;
        if (humidity >= 60 && humidity <= 75) tomato += 2;
        if (ph >= 5.5 && ph <= 7.5) tomato += 2;
        if (soilMoisture > 30) tomato += 1;

        // BRINJAL
        if (temp >= 20 && temp <= 35) brinjal += 2;
        if (humidity >= 60 && humidity <= 80) brinjal += 2;
        if (ph >= 5.5 && ph <= 6.8) brinjal += 2;
        if (soilMoisture > 30) brinjal += 1;

        // CHILLI
        if (temp >= 18 && temp <= 32) chilli += 2;
        if (humidity >= 50 && humidity <= 70) chilli += 2;
        if (ph >= 6.0 && ph <= 7.5) chilli += 2;
        if (soilMoisture > 30) chilli += 1;

        // OKRA
        if (temp >= 22 && temp <= 35) okra += 2;
        if (humidity >= 60 && humidity <= 70) okra += 2;
        if (ph >= 6.0 && ph <= 6.8) okra += 2;
        if (soilMoisture > 30) okra += 1;

        // Find max
        int max = Math.max(
                Math.max(tomato, brinjal),
                Math.max(chilli, okra)
        );

        if (max == tomato) return "Tomato is Best Suitable";
        if (max == brinjal) return "Brinjal is Best Suitable";
        if (max == chilli) return "Chilli is Best Suitable";
        return "Okra is Best Suitable";
    }
}
