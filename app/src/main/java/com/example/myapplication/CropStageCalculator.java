package com.example.myapplication;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CropStageCalculator {

    public static String getStage(String crop, String startDate) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date sowingDate = sdf.parse(startDate);
            Date today = new Date();

            long diff = today.getTime() - sowingDate.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (crop.equalsIgnoreCase("tomato")) {
                if (days <= 15) return "Seedling";
                if (days <= 35) return "Vegetative";
                if (days <= 55) return "Flowering";
                return "Fruiting";
            }
            if (crop.equalsIgnoreCase("brinjal")) {
                if (days <= 20) return "Seedling";
                if (days <= 45) return "Vegetative";
                if (days <= 70) return "Flowering";
                return "Fruiting";
            }
            if (crop.equalsIgnoreCase("chilli")) {
                if (days <= 25) return "Seedling";
                if (days <= 60) return "Vegetative";
                if (days <= 90) return "Flowering";
                return "Fruiting";
            }
            if (crop.equalsIgnoreCase("OKRA")) {
                if (days <= 15) return "Germination";
                if (days <= 30) return "Vegetative";
                if (days <= 45) return "Flowering";
                return "Fruiting";
            }

        } catch (Exception e) {
            return "Invalid Date";
        }

        return "Unknown";
    }
}

