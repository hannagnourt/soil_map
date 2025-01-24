package com.example.soil_map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Represents the root response object
public class InfoResponse {
    @SerializedName("features")
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }
}

class Feature {
    @SerializedName("properties")
    private Property properties;

    public Property getProperties() {
        return properties;
    }
}

class Property {
    @SerializedName("GRAY_INDEX")
    private String value;

    public String getValue() {
        return value;
    }
}
