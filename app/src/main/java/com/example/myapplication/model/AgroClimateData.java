package com.example.myapplication.model;
public class AgroClimateData {
    public boolean rainExpected;
    public int hoursAhead;
    public final float temperature;
    public final float humidity;
    public final float rainMm;
    public final boolean fromSensor;
    public AgroClimateData(
            boolean rainExpected,
            int hoursAhead,
            float temperature,
            float humidity,
            float rainMm,
            boolean fromSensor
    ) {
        this.rainExpected = rainExpected;
        this.hoursAhead = hoursAhead;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainMm = rainMm;
        this.fromSensor = fromSensor;
    }
}
