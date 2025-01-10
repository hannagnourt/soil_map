package com.example.soil_map;

import static com.example.soil_map.LayerNameMapper.getFullName;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LegendActivity extends AppCompatActivity {
    String currentLayer = "";
    String unit = "";

    ImageView imageView;
    ImageButton layersButton;
    ImageButton basemapButton;
    ImageButton legendButton;

    ImageButton homeButton;

    TextView legendUnit;
    LayerRepository layerRepository = new LayerRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_legend);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        currentLayer = intent.getStringExtra("current layer");
        unit = intent.getStringExtra("unit");

        legendUnit = findViewById(R.id.legendUnit);
        if (unit != null)
        {
            legendUnit.setText(unit);
            legendUnit.setVisibility(View.VISIBLE);
        }
        else
            unit = "";


        imageView = findViewById(R.id.legendImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (currentLayer != null && !currentLayer.isEmpty())
        {
            if (!currentLayer.isEmpty())
            {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                String map = currentLayer.split("_")[0];
                String imageURL = "https://maps.isric.org/mapserv?map=/map/" + map +
                        ".map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic" +
                        "&LAYER=" + currentLayer +
                        "&FORMAT=image/png";
                Log.d("image url", imageURL);
                Glide.with(this)
                        .load(imageURL)
                        .fitCenter()
                        .into(imageView);

                if (unit.isEmpty())
                {
                    String[] t = currentLayer.split("_");
                    layerRepository.fetchUnit(t[0], t[1]).observe(this, s -> {
                        legendUnit.setText(getFullName(t[0]) + " - Depth: " + t[1] + "\n" + "Unit: " + s);
                    });
                }
            }
            else
            {
                imageView.setVisibility(View.GONE);
                legendUnit.setVisibility(View.GONE);
            }
        }

        layersButton = findViewById(R.id.layersButton);
        layersButton.setOnClickListener(v -> {
            openLayerSelection(v);
        });

        basemapButton = findViewById(R.id.basemapButton);
        basemapButton.setOnClickListener(v -> {
            // Handle basemap button click event
            openBasemapSelection(v);
        });

        legendButton = findViewById(R.id.legendButton);
        legendButton.setOnClickListener(v -> {
            // Handle legend button click event
            openMain(v);
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            // Handle home button click event
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

    public void openBasemapSelection(View view) {
        Intent intent = new Intent(getApplicationContext(), BasemapSelection.class);
        intent.putExtra("current layer", currentLayer);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle the new intent, e.g., update UI with new data
        setIntent(intent);

        unit = intent.getStringExtra("unit");
        if (unit != null)
            legendUnit.setText(unit);
        else
            unit = "";

        String temp = intent.getStringExtra("current layer");

        if (temp != null)
        {
            currentLayer = temp;

            if (!currentLayer.isEmpty())
            {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                String map = currentLayer.split("_")[0];
                String imageURL = "https://maps.isric.org/mapserv?map=/map/" + map +
                        ".map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic" +
                        "&LAYER=" + currentLayer +
                        "&FORMAT=image/png";
                Log.d("image url", imageURL);
                Glide.with(this)
                        .load(imageURL)
                        .fitCenter()
                        .into(imageView);
                imageView.setVisibility(View.VISIBLE);

                if (unit.isEmpty())
                {
                    String[] t = currentLayer.split("_");
                    layerRepository.fetchUnit(t[0], t[1]).observe(this, s -> {
                        legendUnit.setText(getFullName(t[0]) + " - Depth: " + t[1] + "\n" + "Unit " + s);
                    });
                    legendUnit.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                imageView.setVisibility(View.GONE);
                legendUnit.setVisibility(View.GONE);
            }
        }
    }
}