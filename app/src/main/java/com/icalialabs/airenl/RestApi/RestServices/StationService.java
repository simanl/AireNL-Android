package com.icalialabs.airenl.RestApi.RestServices;

import com.icalialabs.airenl.Models.Station;

import java.util.List;

import retrofit.Call;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Compean on 23/09/15.
 */
public interface StationService {
    @GET("stations?include=last_measurement,current_forecasts&fields[forescasts]=starts_at_rfc822,ends_at_rfc822")
    Call<List<Station>> getAllStations();

    @GET("stations/nearest_from?include=last_measurement,current_forecasts")
    Call<Station> getNearestStationFrom(@Query("latlon") String latlon);

    @GET("stations/{station}?include=last_measurement,current_forecasts")
    Call<Station> getStation(@Path("station") String id);
}
