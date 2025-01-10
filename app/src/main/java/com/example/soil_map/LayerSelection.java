package com.example.soil_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LayerSelection extends AppCompatActivity {
    RecyclerView recyclerViewLayers;
    LayerAdapter layerAdapter;
    List<LayerStructure> layers = new ArrayList<>();
    LayerViewModel layerViewModel;
    LayerNameMapper layerNameMapper = new LayerNameMapper();

    Button btnAddLayer;
    ImageButton layersButton;
    ImageButton basemapButton;
    ImageButton legendButton;

    ImageButton homeButton;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layer_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layer_selection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewLayers = findViewById(R.id.recyclerView_layers);

        layerNameMapper.initialize(this);
        layerAdapter = new LayerAdapter(layers, LayerStructure -> {

        }, layerNameMapper);

        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLayers.setAdapter(layerAdapter);

        if (layers.isEmpty())
        {
            layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
            layerViewModel.getLayersLiveData().observe(this, layerStructures -> {
                layers.addAll(layerStructures);
                layerAdapter.notifyDataSetChanged();
            });
        }

        btnAddLayer = findViewById(R.id.btn_add_layer);
        btnAddLayer.setOnClickListener(v -> {
            // Handle add layer button click event
            layers.add(new LayerStructure());
            layerAdapter.notifyDataSetChanged();
        });

        layersButton = findViewById(R.id.layersButton);
        layersButton.setOnClickListener(v -> {
            // Handle layers button click event
            openMain(v);
        });

        basemapButton = findViewById(R.id.basemapButton);
        basemapButton.setOnClickListener(v -> {
            // Handle basemap button click event
            openBasemapSelection(v);
        });

        legendButton = findViewById(R.id.legendButton);
        legendButton.setOnClickListener(v -> {
            // Handle legend button click event
            openLegend(v);
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            // Handle home button click event
            openMain(v);
        });
    }

    private void openBasemapSelection(View view) {
        Intent intent = new Intent(getApplicationContext(), BasemapSelection.class);
        intent.putExtra("current layer", getCurrentLayer());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private String getCurrentLayer(){
        String currentLayer = "";
        boolean found = false;

        for (LayerStructure layer : layers)
        {
            for (Value value : layer.getLayerStructure()) {
                if (value.getVisibility()) {
                    currentLayer = layer.getProperty() + "_" + value.getRange() + "_mean";
                    found = true;
                    break;
                }
            }

            if (found)
                break;
        }

        return currentLayer;
    }


    private void openMain(View view){
        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
        intentMain.putExtra("current layer", getCurrentLayer());
        intentMain.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentMain);
    }

    private void openLegend(View view){

        Intent intentLegend = new Intent(getApplicationContext(), LegendActivity.class);
        String layer = getCurrentLayer();
        intentLegend.putExtra("current layer", layer);
        if (!layer.isEmpty())
            intentLegend.putExtra("unit", getUnit());
        intentLegend.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentLegend);
    }

    public String getUnit(){
        String currentLayer = getCurrentLayer();
        String[] temp = currentLayer.split("_");
        LayerRepository layerRepository = new LayerRepository();
        AtomicReference<String> unit = new AtomicReference<>("");
        layerRepository.fetchUnit(temp[0], temp[1]).observe(this, s -> {
            unit.set(s);
        });

        return unit.get();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle the new intent, e.g., update UI with new data
        setIntent(intent);
        Log.d("new intent", "layer selection reused");
    }

}