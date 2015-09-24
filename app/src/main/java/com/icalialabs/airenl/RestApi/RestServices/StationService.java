package com.icalialabs.airenl.RestApi.RestServices;

import com.icalialabs.airenl.Models.Station;

import java.util.List;

import retrofit.Call;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;

/**
 * Created by Compean on 23/09/15.
 */
public interface StationService {
    @GET("stations?include=last_measurement")
    Call<List<Station>> getAll();
}
