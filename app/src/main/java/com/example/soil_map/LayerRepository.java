package com.example.soil_map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LayerRepository {

    private final LayerAPI isricApi;
    private MutableLiveData<List<LayerStructure>> liveData = new MutableLiveData<>();

    public LayerRepository() {
        // Initialize Retrofit or your preferred networking library
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rest.isric.org/soilgrids/v2.0/properties/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        isricApi = retrofit.create(LayerAPI.class);
        Log.d("LayerRepository", "LayerRepository created");
    }

    public LiveData<List<LayerStructure>> fetchLayers() {
        isricApi.getLayers().enqueue(new Callback<LayersResponse>() {
            @Override
            public void onResponse(Call<LayersResponse> call, Response<LayersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LayersResponse body = response.body();
                    liveData.setValue(body.getLayers());
                } else {
                    liveData.setValue(null); // Handle error scenario
                }
            }

            @Override
            public void onFailure(Call<LayersResponse> call, Throwable t) {
                liveData.setValue(new ArrayList<>()); // Handle failure
                Log.e("API Error", "Error fetching layers", t);
            }
        });

        return liveData;
    }

    public LiveData<String> fetchInfo(double longtitude, double latitude, String property, String depth) {
        MutableLiveData<String> info = new MutableLiveData<>();
        isricApi.getInfo(longtitude, latitude, property, depth, "mean").enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InfoResponse body = response.body();

                    String unit = body.getProperties().getLayers().get(0).getUnitMeasure().getUnit();
                    String v = body.getProperties().getLayers().get(0).getDepths().get(0).getValues().getMean();
                    if (v != null)
                        info.setValue(v + " " + unit);
                    else
                        info.setValue("no info");
                } else {
                    info.setValue("no info");
                }
            }

            @Override
            public void onFailure(Call<InfoResponse> call, Throwable t) {
                Log.e("Info API Error", "Error fetching info", t);
            }
        });

        return info;
    }

    public LiveData<String> fetchUnit(String property, String depth){
        MutableLiveData<String> unit = new MutableLiveData<>();
        isricApi.getInfo(0, 0, property, depth, "mean").enqueue(new Callback<InfoResponse>() {
            @Override
            public void onResponse(Call<InfoResponse> call, Response<InfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InfoResponse body = response.body();

                    unit.setValue(body.getProperties().getLayers().get(0).getUnitMeasure().getUnit());
                    Log.d("unit", unit.getValue());
                } else {
                    unit.setValue("no unit");
                }
            }

            @Override
            public void onFailure(Call<InfoResponse> call, Throwable t) {
                Log.e("Info API Error", "Error fetching info", t);
            }
        });

        return unit;
    }
}

