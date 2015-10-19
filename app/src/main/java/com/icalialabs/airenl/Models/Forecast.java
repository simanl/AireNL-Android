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
    private String ozone;

    @SerializedName("toracic_particles")
    private String toracicParticles;

    @SerializedName("respirable_particles")
    private String respirableParticles;

    @SerializedName("updated_at")
    private Date updatedAt;

    public Date getForecastedDatetime() {
        return forecastedDatetime;
    }

    public String getOzone() {
        return ozone;
    }

    public String getToracicParticles() {
        return toracicParticles;
    }

    public String getRespirableParticles() {
        return respirableParticles;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
