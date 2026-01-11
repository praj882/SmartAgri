package com.example.myapplication.model;

public class SensorData {

    public float temperature;
    public float humidity;
    public int moisture;
    public String npk;

    // REQUIRED empty constructor for Firebase
    public SensorData() {
    }

    public SensorData(float temperature, float humidity, int moisture, String npk) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.moisture = moisture;
        this.npk = npk;
    }
}

