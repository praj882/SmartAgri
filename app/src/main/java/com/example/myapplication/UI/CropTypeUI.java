package com.example.myapplication.UI;

import com.example.myapplication.rules.CropType;

public enum CropTypeUI {

    SELECT_CROP("Select Crop Name"),
    TOMATO("Tomato"),
    BRINJAL("Brinjal"),
    CHILLI("Chilli");

    private final String label;

    CropTypeUI(String label) {
        this.label = label;
    }

    public CropType toDomain() {
        return this == SELECT_CROP
                ? null
                : CropType.valueOf(name());
    }

    @Override
    public String toString() {
        return label;
    }
}
