package com.example.myapplication.data.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.LatestFarmData;
import com.google.firebase.database.*;

public class FirebaseLatestRepository {

    private final MutableLiveData<LatestFarmData> liveData =
            new MutableLiveData<>();

    public LiveData<LatestFarmData> getLatestData(
            String farmerId,
            String deviceId
    ) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("farmers")
                .child(farmerId)
                .child("devices")
                .child(deviceId)
                .child("latest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    liveData.setValue(null);
                    return;
                }

                LatestFarmData data =
                        snapshot.getValue(LatestFarmData.class);

                liveData.setValue(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return liveData;
    }
}

