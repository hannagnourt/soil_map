package com.example.soil_map;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LayerNameMapper {
    private static Map<String, String> layerNameMap = new HashMap<>();

    public static void initialize(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.layer_names);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                layerNameMap.put(key, jsonObject.getString(key));
            }
        } catch (Exception e) {
            Log.e("LayerNameMapper", "Error loading layer name mappings", e);
        }
    }

    public static String getFullName(String abbreviation) {
        if (layerNameMap.containsKey(abbreviation))
            return layerNameMap.get(abbreviation);

        return abbreviation;
    }
}

