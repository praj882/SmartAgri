package com.example.myapplication.rules;
import com.example.myapplication.model.AgroClimateData;
import com.example.myapplication.model.LatestFarmData;
import com.example.myapplication.model.WeatherForecast;

public class RuleInput {

    public final LatestFarmData data;
    public final AgroClimateData climate;
    public final CropType crop;
    public final CropStage stage;
    public final SoilType soil;

    public AgroClimateData getClimateData() {
        return climate;
    }
    public RuleInput(
            LatestFarmData data,
            AgroClimateData climate,
            CropType crop,
            CropStage stage,
            SoilType soil) {
        this.data = data;
        this.climate = climate;
        this.crop = crop;
        this.stage = stage;
        this.soil = soil;
    }
}
