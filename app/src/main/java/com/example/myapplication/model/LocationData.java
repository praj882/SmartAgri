package com.example.myapplication.model;

public class LocationData {
    public double latitude;
    public double longitude;
    public long timestamp;

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public LocationData() {
    }
    public LocationData(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
