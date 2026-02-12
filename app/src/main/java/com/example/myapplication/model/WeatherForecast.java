package com.example.myapplication.model;

/**
 * Short-term weather forecast data
 * Used for irrigation and disease rules
 */
public class WeatherForecast {

    // 🌧️ Rain
    public boolean rainExpected;
    public float expectedRainMm;
    public int hoursAhead;

    // 🌡️ Climate (from weather API)
    public float temperature;
    public float humidity;

    public WeatherForecast(
            boolean rainExpected,
            float expectedRainMm,
            int hoursAhead,
            float temperature,
            float humidity
    ) {
        this.rainExpected = rainExpected;
        this.expectedRainMm = expectedRainMm;
        this.hoursAhead = hoursAhead;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "rainExpected=" + rainExpected +
                ", expectedRainMm=" + expectedRainMm +
                ", hoursAhead=" + hoursAhead +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
