package com.icalialabs.airenl.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Compean on 23/09/15.
 */
public class Station {
    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("id")
    private String id;

    @SerializedName("last_measurement")
    private Meassurement lastMeassurement;

    @SerializedName("coordinate")
    private Coordinate coordinate;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getId() {
        return id;
    }

    public Meassurement getLastMeassurement() {
        return lastMeassurement;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
