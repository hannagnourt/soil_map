package com.example.soil_map;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LayerAPI {
    @GET("layers")
    Call<LayersResponse> getLayers();

    @GET("query")
    Call<InfoResponse> getInfo(
            @Query("lon") double longtitude,
            @Query("lat") double latitude,
            @Query("property") String property,
            @Query("depth") String depth,
            @Query("value") String value
    );
}

