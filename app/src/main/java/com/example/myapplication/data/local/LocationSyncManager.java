package com.example.myapplication.data.local;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.myapplication.GetLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationSyncManager {

    public static void syncLocation(
            Context context,
            String farmerId,
            String deviceId
    ) {

        if (farmerId == null) {
            Log.w("LOC_SYNC", "FarmerId null – skipping location sync");
            return;
        }

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            Log.w("LOC_SYNC", "Location permission missing");
            return;
        }

        client.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        saveToFirebase(farmerId, deviceId, location);
                    } else {
                        Log.w("LOC_SYNC", "Last location null – requesting fresh update");
                        requestFreshLocation(context, farmerId, deviceId);
                    }
                });
    }

    private static void requestFreshLocation(
            Context context,
            String farmerId,
            String deviceId
    ) {
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(context);

        LocationRequest request = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.requestLocationUpdates(
                request,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult result) {
                        Location location = result.getLastLocation();
                        if (location != null) {
                            saveToFirebase(farmerId, deviceId, location);
                        }
                        client.removeLocationUpdates(this);
                    }
                },
                Looper.getMainLooper()
        );
    }

    private static void saveToFirebase(
            String farmerId,
            String deviceId,
            Location location
    ) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("farmers")
                .child(farmerId)
                .child("devices")
                .child(deviceId)
                .child("location");

        Map<String, Object> map = new HashMap<>();
        map.put("latitude", location.getLatitude());
        map.put("longitude", location.getLongitude());
        map.put("updatedAt", System.currentTimeMillis());
        map.put("source", "GPS");
        ref.setValue(map)
                .addOnSuccessListener(v ->
                        Log.d("LOC_SYNC", "Location saved"))
                .addOnFailureListener(e ->
                        Log.e("LOC_SYNC", "Location save failed", e));
    }
}


