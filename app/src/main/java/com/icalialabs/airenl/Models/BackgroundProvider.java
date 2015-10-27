package com.icalialabs.airenl.Models;

import com.icalialabs.airenl.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Compean on 27/10/15.
 */
public class BackgroundProvider {

    public enum DayFraction {
        Day, Sunset, Sunrise, Night
    }

    private DayFraction fraction;

    private BackgroundProvider(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("HH.mm");
        Float numericTime = Float.parseFloat(dateFormat.format(date));

        if (numericTime >= 9.00 && numericTime <= 18.00) {
            fraction = DayFraction.Day;
        } else if (numericTime >= 6.00 && numericTime <= 9.00) {
            fraction = DayFraction.Sunrise;
        } else if (numericTime >= 18.00 && numericTime <= 21.00) {
            fraction = DayFraction.Sunset;
        } else {
            fraction = DayFraction.Night;
        }
    }

    public DayFraction getDayFraction() {
        return fraction;
    }

    public static BackgroundProvider providerWithDate(Date date) {
        return new BackgroundProvider(date);
    }

    public int getBackgroundResource() {
        switch (fraction) {
            case Day:
                return R.drawable.fondodia;
            case Sunrise:
            case Sunset:
                return R.drawable.fondosunset;
            case Night:
                return R.drawable.fondonoche;
            default:
                return -1;
        }
    }

    public String getBackgroundLocation() {
        switch (fraction) {
            case Day:
                return "Cerro de la Silla, Monterrey, NL.";
            case Sunrise:
            case Sunset:
                return "San Pedro GG, Monterrey";
            case Night:
                return "Mesa del Oso, Nuevo Leon";
            default:
                return "";
        }
    }
}
