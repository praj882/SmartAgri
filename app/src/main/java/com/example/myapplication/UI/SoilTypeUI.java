package com.example.myapplication.UI;

import com.example.myapplication.rules.SoilType;

public enum SoilTypeUI {
    SELECT_SOIL("Select Soil Type"),
    SANDY("Sandy"),
    CLAY("Clay"),
    LOAMY("Loamy");

    private final String label;

    SoilTypeUI(String label) {
        this.label = label;
    }

    public SoilType toDomain() {
        return this == SELECT_SOIL ? null : SoilType.valueOf(name());
    }

    @Override
    public String toString() {
        return label;
    }
}

