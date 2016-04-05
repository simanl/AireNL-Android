package com.icalialabs.airenl.Models;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Compean on 12/10/15.
 */
public class Forecast implements Serializable {
    private static final long serialVersionUID = 1645698L;

    @SerializedName("forecasted_datetime")
    private Date forecastedDatetime;

    @SerializedName("starts_at_rfc822")
    private Date startsAt;

    @SerializedName("ends_at_rfc822")
    private Date endsAt;


    @SerializedName("ozone_index")
    private String ozoneIndex;

    @SerializedName("toracic_particles_index")
    private String toracicParticlesIndex;

    @SerializedName("respirable_particles_index")
    private String respirableParticlesIndex;

    @SerializedName("ozone_category")
    private String ozoneCategory;

    @SerializedName("toracic_particles_category")
    private String toracicParticlesCategory;

    @SerializedName("respirable_particles_category")
    private String respirableParticlesCategory;


    public Date getForecastedDatetime() {
        return forecastedDatetime;
    }

    public Date getStartsAt() {
        return startsAt;
    }

    public Date getEndsAt() { return endsAt; }

    public String getOzoneIndex() {
        return ozoneIndex;
    }

    public String getToracicParticlesIndex() {
        return toracicParticlesIndex;
    }

    public String getRespirableParticlesIndex() {
        return respirableParticlesIndex;
    }

    public int getOzoneColor() {
        return getColorForCategory(ozoneCategory);
    }

    public int getToracicParticlesColor() {
        return getColorForCategory(toracicParticlesCategory);
    }

    public int getRespirableParticlesColor() {
        return getColorForCategory(respirableParticlesCategory);
    }

    private int getColorForCategory(String category){
        if (category == null) {
            return Color.rgb(170, 170, 170);
        }
        switch (category) {
            case "good": return Color.rgb(81, 205, 84);
            case "regular": return Color.rgb(255,215,0);
            case "bad": return Color.rgb(243,154,53);
            case "very_bad": return Color.rgb(230,65,60);
            case "extremely_bad": return Color.rgb(115,52,135);
        }
        return Color.rgb(170, 170, 170);
    }

    public static Comparator<Forecast> StartDateAscendingComparator = new Comparator<Forecast>() {
        @Override
        public int compare(Forecast lhs, Forecast rhs) {
            return lhs.getStartsAt().before(rhs.getStartsAt())? -1 : 1;
        }
    };
}
