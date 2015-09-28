package com.icalialabs.airenl.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Compean on 24/09/15.
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 1445698L;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public LatLng getLatLong() {
        return new LatLng(latitude, longitude);
    }
}
