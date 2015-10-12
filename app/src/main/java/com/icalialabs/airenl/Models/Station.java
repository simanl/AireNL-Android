package com.icalialabs.airenl.Models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.icalialabs.airenl.AireNL;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Compean on 23/09/15.
 */
public class Station implements Serializable {

    private static final String currentStationFileName = "current_station";
    private static final long serialVersionUID = 1345698L;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("id")
    private String id;

    @SerializedName("last_measurement")
    private Measurement lastMeasurement;

    @SerializedName("coordinate")
    private Coordinate coordinate;

    @SerializedName("current_forecasts")
    private List<Forecast> forecasts;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getId() {
        return id;
    }

    public Measurement getLastMeasurement() {
        return lastMeasurement;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void persistAsCurrentStation() {
        try {
            FileOutputStream fos = AireNL.getContext().openFileOutput(currentStationFileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (Exception exception) {
            System.out.println(exception.getLocalizedMessage());
        }
    }

    public static Station getPersistedCurrentStation() {
        Station station = null;
        try {
            FileInputStream fis = AireNL.getContext().openFileInput(currentStationFileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            station = (Station) is.readObject();
            is.close();
            fis.close();
        } catch (Exception exception) {
            System.out.println(exception.getLocalizedMessage());
        }
        return station;
    }
}
