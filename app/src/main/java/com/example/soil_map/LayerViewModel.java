package com.example.soil_map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class LayerViewModel extends ViewModel {
    private final LayerRepository layerRepository;
    private LiveData<List<LayerStructure>> layersLiveData = new MutableLiveData<>();

    public LayerViewModel() {
        layerRepository = new LayerRepository();
    }

    public LiveData<List<LayerStructure>> getLayersLiveData() {
        layersLiveData = layerRepository.fetchLayers();
        return layersLiveData;
    }
}
