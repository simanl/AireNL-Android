package com.icalialabs.airenl.RestApi;

import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icalialabs.airenl.Models.Station;
import com.icalialabs.airenl.RestApi.RestAdapters.StationsTypeAdapterFactory;
import com.icalialabs.airenl.RestApi.RestServices.StationService;
import retrofit.*;

import com.squareup.okhttp.*;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Compean on 23/09/15.
 */

public class StationRestClient {
    private static final String BASE_URL = "http://198.199.108.152:3000/";
    private StationService stationService;

    public StationRestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new StationsTypeAdapterFactory())
                .create();

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
        stationService = retrofit.create(StationService.class);
    }

    public StationService getStationService() {
        return stationService;
    }
}
