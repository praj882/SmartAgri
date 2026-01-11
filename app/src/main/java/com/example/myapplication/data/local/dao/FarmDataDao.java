package com.example.myapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.data.local.entity.FarmData;

import java.util.List;

@Dao
public interface FarmDataDao {

    @Insert
    void insert(FarmData data);

    @Query("SELECT * FROM farm_data ORDER BY timestamp DESC")
    List<FarmData> getAll();
    @Query("SELECT * FROM farm_data WHERE synced = 0")
    List<FarmData> getUnsyncedData();
    @Query("UPDATE farm_data SET synced = 1 WHERE id = :id")
    void markSynced(int id);
}

