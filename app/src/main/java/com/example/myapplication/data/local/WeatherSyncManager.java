package com.example.myapplication.data.local;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.model.WeatherForecast;
import com.example.myapplication.network.weather.WeatherApi;
import com.example.myapplication.network.weather.OpenWeatherResponse;
import com.example.myapplication.network.weather.WeatherMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Central manager for syncing weather data
 */
public class WeatherSyncManager {

    private static final String TAG = "WEATHER_SYNC";

    public interface CallbackListener {
        void onSuccess(WeatherForecast forecast);
        void onFailure(String reason);
    }

    public static void syncWeather(
            double lat,
            double lon,
            @NonNull WeatherApi api,
            @NonNull CallbackListener listener
    ) {

        if (lat == 0 || lon == 0) {
            listener.onFailure("Invalid location");
            return;
        }

        if (BuildConfig.OPEN_WEATHER_API_KEY == null ||
                BuildConfig.OPEN_WEATHER_API_KEY.isEmpty()) {
            listener.onFailure("API key missing");
            return;
        }

        Log.d(TAG, "Fetching weather for lat=" + lat + ", lon=" + lon);

        api.getCurrentWeather(
                lat,
                lon,
                BuildConfig.OPEN_WEATHER_API_KEY,
                "metric"
        ).enqueue(new Callback<OpenWeatherResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<OpenWeatherResponse> call,
                    @NonNull Response<OpenWeatherResponse> response
            ) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Invalid response: " + response.code());
                    listener.onFailure("Invalid API response");
                    return;
                }

                WeatherForecast forecast =
                        WeatherMapper.toForecast(response.body());

                Log.d(TAG, "Weather mapped: " + forecast);
                listener.onSuccess(forecast);
            }

            @Override
            public void onFailure(
                    @NonNull Call<OpenWeatherResponse> call,
                    @NonNull Throwable t
            ) {
                Log.e(TAG, "Weather API failed", t);
                listener.onFailure(t.getMessage());
            }
        });
    }
}

