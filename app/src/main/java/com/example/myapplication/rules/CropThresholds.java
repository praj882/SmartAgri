package com.example.myapplication.rules;

/**
 * Crop-specific moisture and temperature limits
 */
public class CropThresholds {

    public int minSoilMoisture;
    public int maxSoilMoisture;
    public float maxTemperature;

    public CropThresholds(int minSoilMoisture, int maxSoilMoisture, float maxTemperature) {
        this.minSoilMoisture = minSoilMoisture;
        this.maxSoilMoisture = maxSoilMoisture;
        this.maxTemperature = maxTemperature;
    }

    public static CropThresholds get(CropType crop) {

        switch (crop) {
            case TOMATO:
                return new CropThresholds(35, 75, 35);

            case BRINJAL:
                return new CropThresholds(30, 70, 36);

            case CHILLI:
                return new CropThresholds(40, 80, 34);

            default:
                return new CropThresholds(30, 80, 35);
        }
    }
}

