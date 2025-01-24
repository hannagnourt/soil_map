package com.example.soil_map;

import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LayerAPI {
    @GET("layers")
    Call<LayersResponse> getLayers();

    @GET("wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&FORMAT=application/json" +
            "&EXCEPTIONS=application%2Fvnd.ogc.se_inimage" +
            "&INFO_FORMAt=application/json" +
            "&FEATURE_COUNT=50&X=50&Y=50&SRS=EPSG%3A4326&WIDTH=101&HEIGHT=101&STYLES")
    Call<InfoResponse> getInfo(
            @Query("QUERY_LAYERS") String query,
            @Query("LAYERS") String layer,
            @Query("BBOX") String bbox
    );
}

