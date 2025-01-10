package com.example.soil_map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Represents each "value" in the JSON
class Value {
    @SerializedName("range")
    private String range;
    @SerializedName("values")
    private List<String> values;
    private boolean visibility = false;

    // Getters and setters
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}

// Represents each "layer_structure" in the JSON
class LayerStructure {
    @SerializedName("property")
    private String property;
    @SerializedName("layer_structure")
    private List<Value> layerStructure;
    private boolean isExpanded = false;

    // Getters and setters
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public List<Value> getLayerStructure() {
        return layerStructure;
    }

    public void setLayerStructure(List<Value> layerStructure) {
        this.layerStructure = layerStructure;
    }

    public boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}

// Represents the root "layers" array
public class LayersResponse {
    @SerializedName("layers")
    private List<LayerStructure> layers;

    // Getters and setters
    public List<LayerStructure> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerStructure> layers) {
        this.layers = layers;
    }
}


