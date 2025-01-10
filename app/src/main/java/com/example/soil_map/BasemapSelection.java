package com.example.soil_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BasemapSelection extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList<String> basemaps;

    ImageButton layersButton;
    ImageButton basemapButton;
    ImageButton legendButton;
    ImageButton homeButton;

    String currentLayer = "";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basemap_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        currentLayer = intent.getStringExtra("current layer");

        listView = findViewById(R.id.basemap_listview);
        basemaps = new ArrayList<>();
        basemaps.add("Bản đồ đường phố");
        basemaps.add("Bản đồ vệ tinh");
        basemaps.add("Bản đồ địa hình");
        basemaps.add("Bản đồ vệ tinh và đường phố");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, basemaps);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click event
            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
            intentMain.putExtra("basemap", (position+1));
            intentMain.putExtra("current layer", currentLayer);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intentMain);
        });

        layersButton = findViewById(R.id.layersButton);
        layersButton.setOnClickListener(v -> {
            openLayerSelection(v);
        });

        basemapButton = findViewById(R.id.basemapButton);
        basemapButton.setOnClickListener(v -> {
            // Handle basemap button click event
            openMain(v);
        });

        legendButton = findViewById(R.id.legendButton);
        legendButton.setOnClickListener(v -> {
            openLegend(v);
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            openMain(v);
        });
    }

    public void openMain(View view){
        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
        intentMain.putExtra("current layer", currentLayer);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentMain);
    }


    public void openLayerSelection(View view) {
        Intent intentLayers = new Intent(getApplicationContext(), LayerSelection.class);
        intentLayers.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentLayers);
    }

    public void openLegend(View view) {
        Intent intentLegend = new Intent(getApplicationContext(), LegendActivity.class);
        intentLegend.putExtra("current layer", currentLayer);
        intentLegend.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentLegend);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle the new intent, e.g., update UI with new data
        setIntent(intent);

        currentLayer = intent.getStringExtra("current layer");

        Log.d("new intent", "basemap selection reused");
    }

}