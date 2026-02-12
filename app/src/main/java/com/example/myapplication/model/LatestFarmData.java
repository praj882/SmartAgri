package com.example.myapplication.model;
public class LatestFarmData {

    public float temperature;
    public float humidity;
    public int moisture;

    public int nitrogen;
    public int phosphorus;
    public int potassium;


    public String source;     // WIFI | BLE | MANUAL
    public long timestamp;
    public WeatherForecast forecast;
    // Required empty constructor for Firebase
    public LatestFarmData() {
    }
}

