package com.example.myapplication;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
public class GetLocation {

    public static String getAddressFromLocation(
            Context context,
            double lat,
            double lon
    ) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !(addresses.isEmpty())) {
                Address address = addresses.get(0);

                String state = address.getAdminArea();
                String district = address.getSubAdminArea();
                String village = address.getLocality();

                Log.d("LOCATION", "State: " + state);
                Log.d("LOCATION", "District: " + district);
                Log.d("LOCATION", "Village: " + village);

                return state;
            }
        } catch (IOException e) {
            Log.e("LOCATION", "Geocoder failed", e);
        }
        return "unknown";
    }
}

