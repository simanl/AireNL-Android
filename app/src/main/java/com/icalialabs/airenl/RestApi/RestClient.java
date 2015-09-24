package com.icalialabs.airenl.RestApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icalialabs.airenl.RestApi.RestAdapters.JSONApiTypeAdapterFactory;
import com.icalialabs.airenl.RestApi.RestServices.StationService;
import retrofit.*;

import com.squareup.okhttp.*;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Compean on 23/09/15.
 */

public class RestClient {
    private static final String BASE_URL = "http://198.199.108.152:3000/";
    private Retrofit retrofit;
    private StationService stationService;

    public RestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new JSONApiTypeAdapterFactory())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'")
                .create();

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
//        stationService = retrofit.create(StationService.class);
    }

    public StationService getStationService() {
        if (stationService == null) {
            stationService = retrofit.create(StationService.class);
        }
        return stationService;
    }
}
