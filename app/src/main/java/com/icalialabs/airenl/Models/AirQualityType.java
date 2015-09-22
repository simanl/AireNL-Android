package com.icalialabs.airenl.Models;

import android.graphics.Color;

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

    @Override
    public String toString() {
        if (id == 0) {
            return "none";
        }
        return AireNL.getContext().getString(id);
    }
}
