package com.example.myapplication.data.local.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "farm_data")
public class FarmData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public float moisture;
    public float temperature;
    public float humidity;
    public float nitrogen;
    public float phosphorus;
    public float potassium;

    public long timestamp;
    public String source;   // MANUAL or SENSOR
    public boolean synced;  // false for offline
}
