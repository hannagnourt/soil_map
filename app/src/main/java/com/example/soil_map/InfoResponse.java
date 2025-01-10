package com.example.soil_map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Represents the root response object
public class InfoResponse {
    // The "properties" field in the JSON response
    @SerializedName("properties")
    private Property properties;

    public Property getProperties() {
        return properties;
    }
}

// Represents the "properties" object in the JSON response
class Property {
    @SerializedName("layers")
    private List<Layer> layers;

    public List<Layer> getLayers() {
        return layers;
    }
}

class Layer {
    @SerializedName("unit_measure")
    private UnitMeasure unitMeasure;
    @SerializedName("depths")
    private List<Depth> depths;

    public UnitMeasure getUnitMeasure() {
        return unitMeasure;
    }

    public List<Depth> getDepths() {
        return depths;
    }
}

class UnitMeasure {
    @SerializedName("mapped_units")
    private String unit;

    public String getUnit() {
        return unit;
    }
}

class Depth {
    @SerializedName("values")
    private Values values;

    public Values getValues() {
        return values;
    }
}

class Values {
    @SerializedName("mean")
    private String mean;

    public String getMean() {
        return mean;
    }
}