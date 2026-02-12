package com.example.myapplication.crop;

import com.example.myapplication.rules.CropType;

import java.util.HashMap;
import java.util.Map;

public class CropProfileRegistry {

    private static final Map<CropType, CropProfile> PROFILES = new HashMap<>();

    static {
        register(new TomatoProfile());
        register(new BrinjalProfile());
        register(new ChilliProfile());
    }
    public static CropProfile getOrDefault(CropType crop) {
        CropProfile profile = PROFILES.get(crop);
        return profile != null
                ? profile
                : new UnsupportedCropProfile(crop);
    }
    private static void register(CropProfile profile) {
        PROFILES.put(profile.getCropType(), profile);
    }

    public static CropProfile get(CropType crop) {
        return PROFILES.get(crop);
    }
    public static boolean isSupported(CropType crop) {
        return PROFILES.containsKey(crop);
    }
}

