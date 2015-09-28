package com.icalialabs.airenl.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Compean on 23/09/15.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = 1345698L;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("id")
    private String id;

    @SerializedName("last_measurement")
    private Measurement lastMeasurement;

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

    public Measurement getLastMeasurement() {
        return lastMeasurement;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
