package com.example.soil_map;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LayerViewModel extends ViewModel {
    private final LayerRepository layerRepository;
    private LiveData<List<LayerStructure>> layersLiveData = new MutableLiveData<>();
    private MutableLiveData<LayerStructure> selectedLayer = new MutableLiveData<>();
    private List layers = new ArrayList();

    public LayerViewModel() {
        layerRepository = new LayerRepository();
    }

    public LiveData<List<LayerStructure>> getLayersLiveData() {
        layersLiveData = layerRepository.fetchLayers();
        return layersLiveData;
    }

    public void setSelectedLayer(LayerStructure layer){
        selectedLayer.setValue(layer);
    }

    public LiveData<LayerStructure> getSelectedLayer() {
        return selectedLayer;
    }

    public void setLayers(List<LayerStructure> layers) {
        this.layers = layers;
    }

    public List<LayerStructure> getLayers() {
        return layers;
    }
}
