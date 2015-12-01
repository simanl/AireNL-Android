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
        return new Recomendation(R.drawable.outdoor_recomendation, AireNL.getContext().getString(R.string.outdoors_description));
    }

    public static Recomendation window() {
        return new Recomendation(R.drawable.closed_windows_recomendation, AireNL.getContext().getString(R.string.window_description));
    }

    public static Recomendation run() {
        return new Recomendation(R.drawable.run_recomendation, AireNL.getContext().getString(R.string.run_description));
    }

    public static Recomendation allergy() {
        return new Recomendation(R.drawable.asthma_recomendation, AireNL.getContext().getString(R.string.allergy_description));
    }

    public static Recomendation heartCondition() {
        return new Recomendation(R.drawable.heart_condition_recomendation, AireNL.getContext().getString(R.string.heart_condition_description));
    }

    public static Recomendation gas() {
        return new Recomendation(R.drawable.gas_recomendation, AireNL.getContext().getString(R.string.gas_description));
    }

    public static Recomendation car() {
        return new Recomendation(R.drawable.car_recomendation, AireNL.getContext().getString(R.string.car_description));
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getDescription() {
        return description;
    }
}
