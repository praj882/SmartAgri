package com.example.myapplication.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.myapplication.data.local.entity.FarmData;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FarmDataDao_Impl implements FarmDataDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FarmData> __insertionAdapterOfFarmData;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  public FarmDataDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFarmData = new EntityInsertionAdapter<FarmData>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `farm_data` (`id`,`moisture`,`temperature`,`humidity`,`nitrogen`,`phosphorus`,`potassium`,`timestamp`,`source`,`synced`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final FarmData entity) {
        statement.bindLong(1, entity.id);
        statement.bindDouble(2, entity.moisture);
        statement.bindDouble(3, entity.temperature);
        statement.bindDouble(4, entity.humidity);
        statement.bindDouble(5, entity.nitrogen);
        statement.bindDouble(6, entity.phosphorus);
        statement.bindDouble(7, entity.potassium);
        statement.bindLong(8, entity.timestamp);
        if (entity.source == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.source);
        }
        final int _tmp = entity.synced ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE farm_data SET synced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final FarmData data) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfFarmData.insert(data);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void markSynced(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkSynced.release(_stmt);
    }
  }

  @Override
  public List<FarmData> getAll() {
    final String _sql = "SELECT * FROM farm_data ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfMoisture = CursorUtil.getColumnIndexOrThrow(_cursor, "moisture");
      final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
      final int _cursorIndexOfHumidity = CursorUtil.getColumnIndexOrThrow(_cursor, "humidity");
      final int _cursorIndexOfNitrogen = CursorUtil.getColumnIndexOrThrow(_cursor, "nitrogen");
      final int _cursorIndexOfPhosphorus = CursorUtil.getColumnIndexOrThrow(_cursor, "phosphorus");
      final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
      final List<FarmData> _result = new ArrayList<FarmData>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final FarmData _item;
        _item = new FarmData();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.moisture = _cursor.getFloat(_cursorIndexOfMoisture);
        _item.temperature = _cursor.getFloat(_cursorIndexOfTemperature);
        _item.humidity = _cursor.getFloat(_cursorIndexOfHumidity);
        _item.nitrogen = _cursor.getFloat(_cursorIndexOfNitrogen);
        _item.phosphorus = _cursor.getFloat(_cursorIndexOfPhosphorus);
        _item.potassium = _cursor.getFloat(_cursorIndexOfPotassium);
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        if (_cursor.isNull(_cursorIndexOfSource)) {
          _item.source = null;
        } else {
          _item.source = _cursor.getString(_cursorIndexOfSource);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfSynced);
        _item.synced = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<FarmData> getUnsyncedData() {
    final String _sql = "SELECT * FROM farm_data WHERE synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfMoisture = CursorUtil.getColumnIndexOrThrow(_cursor, "moisture");
      final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
      final int _cursorIndexOfHumidity = CursorUtil.getColumnIndexOrThrow(_cursor, "humidity");
      final int _cursorIndexOfNitrogen = CursorUtil.getColumnIndexOrThrow(_cursor, "nitrogen");
      final int _cursorIndexOfPhosphorus = CursorUtil.getColumnIndexOrThrow(_cursor, "phosphorus");
      final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
      final List<FarmData> _result = new ArrayList<FarmData>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final FarmData _item;
        _item = new FarmData();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.moisture = _cursor.getFloat(_cursorIndexOfMoisture);
        _item.temperature = _cursor.getFloat(_cursorIndexOfTemperature);
        _item.humidity = _cursor.getFloat(_cursorIndexOfHumidity);
        _item.nitrogen = _cursor.getFloat(_cursorIndexOfNitrogen);
        _item.phosphorus = _cursor.getFloat(_cursorIndexOfPhosphorus);
        _item.potassium = _cursor.getFloat(_cursorIndexOfPotassium);
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        if (_cursor.isNull(_cursorIndexOfSource)) {
          _item.source = null;
        } else {
          _item.source = _cursor.getString(_cursorIndexOfSource);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfSynced);
        _item.synced = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
