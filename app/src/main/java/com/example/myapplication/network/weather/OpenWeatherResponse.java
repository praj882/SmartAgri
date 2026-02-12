package com.example.myapplication.network.weather;

import java.util.List;

public class OpenWeatherResponse {
    public List<Weather> weather;
    public Main main;
    public Rain rain;

    public static class Weather {
        public String main;
        public String description;
    }

    public static class Main {
        public float temp;
        public int humidity;
    }

    public static class Rain {
        public float _1h; // rainfall in last 1 hour
    }
}

