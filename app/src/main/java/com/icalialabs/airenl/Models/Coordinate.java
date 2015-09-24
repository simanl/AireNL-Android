package com.icalialabs.airenl.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Compean on 24/09/15.
 */
public class Coordinate {
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public LatLng getLatLong() {
        return new LatLng(latitude, longitude);
    }
}
