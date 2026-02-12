package com.example.myapplication.data.local;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebasePaths {

    private static final FirebaseDatabase db =
            FirebaseDatabase.getInstance();

    public static DatabaseReference device(String farmerId, String deviceId) {
        return db.getReference("farmers")
                .child(farmerId)
                .child("devices")
                .child(deviceId);
    }
    // farmers/{farmerId}/devices/{deviceId}/latestData
    public static DatabaseReference latestData(String farmerId, String deviceId) {
        return device(farmerId, deviceId).child("latest");
    }
    // farmers/{farmerId}/devices/{deviceId}/history
    public static DatabaseReference history(String farmerId, String deviceId) {
        return device(farmerId, deviceId).child("history");
    }
    // farmers/{farmerId}/devices/{deviceId}/location
    public static DatabaseReference location(String farmerId, String deviceId) {
        return device(farmerId, deviceId).child("location");
    }

    // Raw sensor push (ESP → Firebase)
    public static DatabaseReference sensorData() {
        return db.getReference("sensorData");
    }

    // Device heartbeat (online/offline)
    public static DatabaseReference deviceHeartbeat(String farmerId, String deviceId) {
        return db.getReference("farmers")
                .child(farmerId)
                .child("devices")
                .child(deviceId)
                .child("heartbeat");
    }
}

