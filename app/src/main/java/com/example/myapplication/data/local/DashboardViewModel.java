package com.example.myapplication.data.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.data.local.FirebaseLatestRepository;

public class DashboardViewModel extends ViewModel {

    private final FirebaseLatestRepository repository =
            new FirebaseLatestRepository();

    public LiveData<LatestFarmData> getDashboardData(
            String farmerId,
            String deviceId
    ) {
        return repository.getLatestData(farmerId, deviceId);
    }
}

