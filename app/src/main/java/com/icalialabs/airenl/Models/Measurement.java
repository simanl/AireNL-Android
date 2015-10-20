package com.icalialabs.airenl.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Compean on 23/09/15.
 */
public class Measurement implements Serializable {

    private static final long serialVersionUID = 1545698L;

    @SerializedName("id")
    private String id;

    @SerializedName("measured_at_rfc822")
    private Date measuredAt;

    @SerializedName("temperature")
    private Double temperature;

    @SerializedName("relative_humidity")
    private Double relativeHumidity;

    @SerializedName("wind_direction")
    private Integer windDirection;

    @SerializedName("wind_speed")
    private Double windSpeed;

    @SerializedName("imeca_points")
    private Integer imecaPoints;

    @SerializedName("precipitation")
    private Double precipitation;

    @SerializedName("carbon_monoxide")
    private Double carbonMonoxide;

    @SerializedName("nitric_oxide")
    private Double nitricOxide;

    @SerializedName("nitrogen_dioxide")
    private Double nitrogenDioxide;

    @SerializedName("nitrogen_oxides")
    private Double nitrogenOxides;

    @SerializedName("ozone")
    private Double ozone;

    @SerializedName("sulfur_dioxide")
    private Double sulfurDioxide;

    @SerializedName("suspended_particulate_matter")
    private Double suspendedParticulateMatter;

    @SerializedName("respirable_suspended_particles")
    private Double respirableSuspendedParticles;

    @SerializedName("toracic_particles")
    private Double toracicParticles;

    @SerializedName("respirable_particles")
    private Double respirableParticles;

    @SerializedName("fine_particles")
    private Double fineParticles;

    public String getId() {
        return id;
    }

    public Date getMeasuredAt() {
        return measuredAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getRelativeHumidity() {
        return relativeHumidity;
    }

    public Integer getWindDirection() {
        return windDirection;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Integer getImecaPoints() {
        return imecaPoints;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public Double getCarbonMonoxide() {
        return carbonMonoxide;
    }

    public Double getNitricOxide() {
        return nitricOxide;
    }

    public Double getNitrogenDioxide() {
        return nitrogenDioxide;
    }

    public Double getNitrogenOxides() {
        return nitrogenOxides;
    }

    public Double getOzone() {
        return ozone;
    }

    public Double getSulfurDioxide() {
        return sulfurDioxide;
    }

    public Double getSuspendedParticulateMatter() {
        return suspendedParticulateMatter;
    }

    public Double getRespirableSuspendedParticles() {
        return respirableSuspendedParticles;
    }

    public Double getFineParticles() {
        return fineParticles;
    }

    public Double getToracicParticles() {
        return toracicParticles;
    }

    public Double getRespirableParticles() {
        return respirableParticles;
    }


}
