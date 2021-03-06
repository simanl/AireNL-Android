package com.icalialabs.airenl.Models;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.icalialabs.airenl.AireNL;
import com.icalialabs.airenl.R;

import java.util.ArrayList;
import java.util.List;

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
        return Color.rgb(170,170,170);
    }

    public BitmapDescriptor getIcon() {
        switch (id) {
            case R.string.good: return BitmapDescriptorFactory.fromResource(R.drawable.green_marker);
            case R.string.regular: return BitmapDescriptorFactory.fromResource(R.drawable.yellow_marker);
            case R.string.bad: return BitmapDescriptorFactory.fromResource(R.drawable.orange_marker);
            case R.string.very_bad: return BitmapDescriptorFactory.fromResource(R.drawable.red_marker);
            case R.string.extremely_bad: return BitmapDescriptorFactory.fromResource(R.drawable.purple_marker);
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.gray_marker);
    }

    public static AirQualityType qualityTypeWithImecaValue(Integer imeca) {
        if (imeca == null) {
            return NONE;
        } else if (imeca < 50d) {
            return GOOD;
        } else if (isBetween(imeca, 50, 100)){
            return REGULAR;
        } else if (isBetween(imeca, 100, 150)){
            return BAD;
        } else if (isBetween(imeca, 150, 200)){
            return VERY_BAD;
        } else {
            return EXTREMELY_BAD;
        }
    }

    public static AirQualityType qualityTypeWithString(String string) {
        if (string == null) {
            return NONE;
        } else if (string.equals("good")) {
            return GOOD;
        } else if (string.equals("regular")) {
            return REGULAR;
        } else if (string.equals("bad")) {
            return BAD;
        } else if (string.equals("very_bad")) {
            return VERY_BAD;
        } else if (string.equals("extremely_bad")) {
            return EXTREMELY_BAD;
        } else {
            return NONE;
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
            return "N/A";
        }
        return AireNL.getContext().getString(id).toUpperCase();
    }

    public String lowerCaseString() {
        if (id == 0) {
            return "N/A";
        }
        return AireNL.getContext().getString(id).toLowerCase();
    }

    public List<Recomendation> recommendationsImageIds()
    {
        final List<Recomendation> imageIds = new ArrayList<Recomendation>();

        switch (id) {
            case R.string.good:

                imageIds.add(Recomendation.exercise());
                imageIds.add(Recomendation.outdoors());
                imageIds.add(Recomendation.sensible());
                break;

            case R.string.regular:

                imageIds.add(Recomendation.exercise());
                imageIds.add(Recomendation.outdoors());
                imageIds.add(Recomendation.sensibleLimit());
                break;

            case R.string.bad:

                imageIds.add(Recomendation.exerciseLimit());
                imageIds.add(Recomendation.outdoorsLimit());
                imageIds.add(Recomendation.sensibleNo());
                break;

            case R.string.very_bad:

                imageIds.add(Recomendation.exerciseNo());
                imageIds.add(Recomendation.outdoorsNo());
                imageIds.add(Recomendation.window());
                imageIds.add(Recomendation.sensibleNo());
                imageIds.add(Recomendation.heartLimit());
                imageIds.add(Recomendation.carLimit());
                imageIds.add(Recomendation.fuelNo());
                imageIds.add(Recomendation.smokingLimit());
                break;

            case R.string.extremely_bad:

                imageIds.add(Recomendation.exerciseNo());
                imageIds.add(Recomendation.outdoorsNo());
                imageIds.add(Recomendation.window());
                imageIds.add(Recomendation.sensibleNo());
                imageIds.add(Recomendation.heartLimit());
                imageIds.add(Recomendation.carNo());
                imageIds.add(Recomendation.fuelNo());
                imageIds.add(Recomendation.smokingNo());
                break;

        }

        return imageIds;

    }
}
