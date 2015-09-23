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
}
