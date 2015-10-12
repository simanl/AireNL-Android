package com.icalialabs.airenl.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Compean on 12/10/15.
 */
public class Forecast implements Serializable {
    private static final long serialVersionUID = 1645698L;

    @SerializedName("forecasted_datetime")
    private Date forecastedDatetime;

    @SerializedName("ozone")
    private Double ozone;

    @SerializedName("toracic_particles")
    private Double toracicParticles;

    @SerializedName("respirable_particles")
    private Double respirableParticles;

    @SerializedName("updated_at")
    private Date updatedAt;

    public Date getForecastedDatetime() {
        return forecastedDatetime;
    }

    public Double getOzone() {
        return ozone;
    }

    public Double getToracicParticles() {
        return toracicParticles;
    }

    public Double getRespirableParticles() {
        return respirableParticles;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
