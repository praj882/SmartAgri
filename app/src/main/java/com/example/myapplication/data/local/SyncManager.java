package com.example.myapplication.data.local;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.entity.FarmData;
import com.example.myapplication.data.local.FirebaseUploadModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myapplication.data.local.SessionManager;

import java.util.List;

public class SyncManager {
    //String farmerId = SessionManager.getFarmerId();
    //String deviceId = "device_001";

    public static void sync(Context context) {

        String farmerId = SessionManager.getFarmerId();

        if (farmerId == null) {
            Log.e("SYNC", "Farmer not logged in. Sync aborted.");
            return;
        }

        String deviceId = "device_001";

        new Thread(() -> {

            List<FarmData> list =
                    AppDatabase.getInstance(context)
                            .farmDataDao()
                            .getUnsyncedData();

            DatabaseReference baseRef =
                    FirebaseDatabase.getInstance()
                            .getReference("farmers")
                            .child(farmerId)
                            .child("devices")
                            .child(deviceId);

            for (FarmData f : list) {

                FirebaseUploadModel upload = mapToFirebase(f);

                // 1️⃣ Update latest (NO push)
                baseRef.child("latest").setValue(upload);

                // 2️⃣ Update history (timestamp-based key)
                baseRef.child("history")
                        .child(String.valueOf(upload.timestamp))
                        .setValue(upload)
                        .addOnSuccessListener(aVoid -> {
                            new Thread(() ->
                                    AppDatabase.getInstance(context)
                                            .farmDataDao()
                                            .markSynced(f.id)
                            ).start();
                        })
                        .addOnFailureListener(e ->
                                Log.e("SYNC", "Firebase sync failed", e)
                        );
            }
        }).start();
    }

    private static FirebaseUploadModel mapToFirebase(FarmData f) {

        FirebaseUploadModel m = new FirebaseUploadModel();

        m.temperature = f.temperature;
        m.humidity = f.humidity;
        m.moisture = f.moisture;

        m.nitrogen = f.nitrogen;
        m.phosphorus = f.phosphorus;
        m.potassium = f.potassium;

        m.timestamp = f.timestamp;
        m.source = f.source;

        return m;
    }
}

