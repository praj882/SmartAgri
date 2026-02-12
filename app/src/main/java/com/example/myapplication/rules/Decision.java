package com.example.myapplication.rules;

/**
 * Decision codes produced by RuleEngine.
 * These are deterministic and safe.
 */
public enum Decision {

    // Irrigation
    WATER_REQUIRED,
    OVER_WATERED,
    HEAT_STRESS,

    // Fertilization (alert only)
    FERTILIZER_REQUIRED,
    LOW_NUTRIENT_RISK,

    // Pest (risk only)
    PEST_RISK_HIGH,
    PEST_RISK_MEDIUM,

    NORMAL_CONDITION
}
