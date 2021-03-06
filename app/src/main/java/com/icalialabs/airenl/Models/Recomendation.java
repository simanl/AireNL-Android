package com.icalialabs.airenl.Models;

import com.icalialabs.airenl.AireNL;
import com.icalialabs.airenl.R;

/**
 * Created by Compean on 19/10/15.
 */
public class Recomendation {
    int resourceId;
    String description;

    public Recomendation(int resourceId, String description) {
        this.resourceId = resourceId;
        this.description = description;
    }

    public static Recomendation outdoors() {
        return new Recomendation(R.drawable.outdoors, AireNL.getContext().getString(R.string.outdoors));
    }

    public static Recomendation outdoorsLimit() {
        return new Recomendation(R.drawable.outdoors_limit, AireNL.getContext().getString(R.string.outdoors_limit));
    }

    public static Recomendation outdoorsNo() {
        return new Recomendation(R.drawable.outdoors_no, AireNL.getContext().getString(R.string.outdoors_no));
    }

    public static Recomendation exercise() {
        return new Recomendation(R.drawable.exercise, AireNL.getContext().getString(R.string.exercise));
    }

    public static Recomendation exerciseLimit() {
        return new Recomendation(R.drawable.exercise_limit, AireNL.getContext().getString(R.string.exercise_limit));
    }

    public static Recomendation exerciseNo() {
        return new Recomendation(R.drawable.exercise_no, AireNL.getContext().getString(R.string.exercise_no));
    }

    public static Recomendation sensible() {
        return new Recomendation(R.drawable.sensible, AireNL.getContext().getString(R.string.sensible));
    }

    public static Recomendation sensibleLimit() {
        return new Recomendation(R.drawable.sensible_limit, AireNL.getContext().getString(R.string.sensible_limit));
    }

    public static Recomendation sensibleNo() {
        return new Recomendation(R.drawable.sensible_no, AireNL.getContext().getString(R.string.sensible_no));
    }

    public static Recomendation carLimit() {
        return new Recomendation(R.drawable.car_limit, AireNL.getContext().getString(R.string.car_limit));
    }

    public static Recomendation carNo() {
        return new Recomendation(R.drawable.car_no, AireNL.getContext().getString(R.string.car_no));
    }

    public static Recomendation smokingLimit() {
        return new Recomendation(R.drawable.smoking_limit, AireNL.getContext().getString(R.string.smoking_limit));
    }

    public static Recomendation smokingNo() {
        return new Recomendation(R.drawable.smoking_no, AireNL.getContext().getString(R.string.smoking_no));
    }

    public static Recomendation window() {
        return new Recomendation(R.drawable.window, AireNL.getContext().getString(R.string.window));
    }

    public static Recomendation heartLimit() {
        return new Recomendation(R.drawable.heart_limit, AireNL.getContext().getString(R.string.heart_limit));
    }

    public static Recomendation fuelNo() {
        return new Recomendation(R.drawable.fuel_no, AireNL.getContext().getString(R.string.fuel_no));
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getDescription() {
        return description;
    }
}
