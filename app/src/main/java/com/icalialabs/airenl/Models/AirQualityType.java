package com.icalialabs.airenl.Models;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.icalialabs.airenl.AireNL;
import com.icalialabs.airenl.R;

/**
 * Created by Compean on 10/09/15.
 */
public enum AirQualityType {
    GOOD (R.string.good),
    REGULAR (R.string.regular),
    BAD (R.string.bad),
    VERY_BAD (R.string.very_bad),
    EXTREMELY_BAD (R.string.extremely_bad),
    NONE (0)
    ;

    private final int id;

    private AirQualityType(int id){
        this.id = id;
    }

    public int color() {
        switch (id) {
            case R.string.good: return Color.rgb(81, 205, 84);
            case R.string.regular: return Color.rgb(255,215,0);
            case R.string.bad: return Color.rgb(243,154,53);
            case R.string.very_bad: return Color.rgb(230,65,60);
            case R.string.extremely_bad: return Color.rgb(115,52,135);
        }
        return Color.TRANSPARENT;
    }

    public BitmapDescriptor getIcon() {
        switch (id) {
            case R.string.good: return BitmapDescriptorFactory.fromResource(R.drawable.green_marker);
            case R.string.regular: return BitmapDescriptorFactory.fromResource(R.drawable.yellow_marker);
            case R.string.bad: return BitmapDescriptorFactory.fromResource(R.drawable.orange_marker);
            case R.string.very_bad: return BitmapDescriptorFactory.fromResource(R.drawable.red_marker);
            case R.string.extremely_bad: return BitmapDescriptorFactory.fromResource(R.drawable.purple_marker);
        }
        return null;
    }

    public static AirQualityType qualityTypeWithImecaValue(Integer imeca) {
        if (imeca < 130d) {
            return GOOD;
        } else if (isBetween(imeca, 130, 140)){
            return REGULAR;
        } else if (isBetween(imeca, 140, 165)){
            return BAD;
        } else if (isBetween(imeca, 165, 185)){
            return VERY_BAD;
        } else {
            return EXTREMELY_BAD;
        }
    }

    private static boolean isBetween(Integer x, Integer lower, Integer upper){
        return lower <= x && x < upper;
    }

    public static AirQualityType qualityType(int id) {
        switch (id) {
            case R.string.good: return GOOD;
            case R.string.regular: return REGULAR;
            case R.string.bad: return BAD;
            case R.string.very_bad: return VERY_BAD;
            case R.string.extremely_bad: return EXTREMELY_BAD;
        }
        return NONE;
    }

    public int getStringId() {
        return id;
    }

    @Override
    public String toString() {
        if (id == 0) {
            return "none";
        }
        return AireNL.getContext().getString(id).toUpperCase();
    }
}
