package com.example.myapplication.rules;

import com.example.myapplication.model.AgroClimateData;
import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.model.WeatherForecast;

public class AgroClimateResolver {
    private static final long SENSOR_TIMEOUT_MS = 15 * 60 * 1000;
    public static AgroClimateData resolve(
            LatestFarmData LtFarmdata,
            WeatherForecast weather
    ) {
        boolean rainExpected =
                weather != null ? weather.rainExpected : false;
        int hoursAhead =
                weather != null ? weather.hoursAhead : 0;
        long now = System.currentTimeMillis();
        boolean LtFarmdataFresh =
                LtFarmdata != null
                        && LtFarmdata.temperature > 0
                        && LtFarmdata.humidity > 0
                        && (now - LtFarmdata.timestamp) < SENSOR_TIMEOUT_MS;
                        //&& sensor.isOnline; // optional but recommended
        boolean UseLtFarmdata = LtFarmdataFresh;

        float temperature = UseLtFarmdata
                ? LtFarmdata.temperature
                : (weather != null ? weather.temperature : 0);

        float humidity = UseLtFarmdata
                ? LtFarmdata.humidity
                : (weather != null ? weather.humidity : 0);

        float rainMm =
                weather != null ? weather.expectedRainMm : 0;

        return new AgroClimateData(
                rainExpected,
                hoursAhead,
                temperature,
                humidity,
                rainMm,
                UseLtFarmdata
        );
    }
}
