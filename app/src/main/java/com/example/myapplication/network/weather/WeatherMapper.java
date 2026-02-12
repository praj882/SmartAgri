package com.example.myapplication.network.weather;

import com.example.myapplication.model.WeatherForecast;
import com.example.myapplication.network.weather.OpenWeatherResponse;

public class WeatherMapper {

    public static WeatherForecast toForecast(OpenWeatherResponse res) {

        boolean rainExpected = false;
        float rainMm = 0;

        if (res.weather != null && !res.weather.isEmpty()) {
            String main = res.weather.get(0).main;
            rainExpected = "Rain".equalsIgnoreCase(main);
        }

        if (res.rain != null) {
            rainMm = res.rain._1h;
        }

        return new WeatherForecast(
                rainExpected,
                rainMm,
                24,
                res.main.temp,
                res.main.humidity
        );
    }
}
