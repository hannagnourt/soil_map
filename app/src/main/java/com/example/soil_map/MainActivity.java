package com.example.soil_map;

import static com.example.soil_map.LayerNameMapper.getFullName;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteFragment;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private TileOverlay tileOverlay;

    ImageButton layersButton;
    FloatingActionButton searchButton;
    SearchView searchView;
    LinearLayout searchBar;

    ImageButton basemapButton;
    ImageButton legendButton;

    TextView tapTextView;
    
    int maptype = 1;
    List<LayerStructure> layers = new ArrayList<>();
    SupportMapFragment mapFragment;
    String currentLayer = "";
    LatLng tapPoint;

    String unit = "";
    String index = "";

    LayerRepository layerRepository = new LayerRepository();
    LayerViewModel layerViewModel;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layer_selection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        layerViewModel.setSelectedLayer(null);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        layersButton = findViewById(R.id.layersButton);
        layersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLayerSelection(v);
            }
        });

        searchButton = findViewById(R.id.searchButton);
        searchBar = findViewById(R.id.search_bar);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBar.getVisibility() == View.VISIBLE)
                    searchBar.setVisibility(View.GONE);
                else
                {
                    searchBar.setVisibility(View.VISIBLE);

                    String apiKey = BuildConfig.PLACES_API_KEY;

                    if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
                        Log.e("Places test", "No api key");
                        finish();
                        return;
                    }

                    if (!Places.isInitialized()) {
                        Places.initialize(getApplicationContext(), apiKey);
                    }

                    AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            // Get place details here
                            LatLng selectedLocation = place.getLatLng();
                            Toast.makeText(MainActivity.this, "Selected: " + place.getName(), Toast.LENGTH_SHORT).show();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 10));
                            mMap.addMarker(new MarkerOptions().position(selectedLocation).title(place.getName()));
                        }

                        @Override
                        public void onError(Status status) {
                            Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        basemapButton = findViewById(R.id.basemapButton);
        basemapButton.setOnClickListener(v -> openBasemapSelection(v));

        legendButton = findViewById(R.id.legendButton);
        legendButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), LegendActivity.class);
//            intent.putExtra("current layer", currentLayer);
//            intent.putExtra("unit", unit);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
            LegendFragment legendFragment = LegendFragment.newInstance();
            legendFragment.show(getSupportFragmentManager(), "legendFragment");
        });

        tapTextView = findViewById(R.id.tapText);
        tapTextView.setVisibility(View.GONE);
    }

    public void openLayerSelection(View view) {
//        Intent intent = new Intent(getApplicationContext(), LayerSelection.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(intent);
        LayerSelectionFragment layerSelectionFragment = LayerSelectionFragment.newInstance();
        layerSelectionFragment.show(getSupportFragmentManager(), "layerSelectionFragment");

        layerViewModel.getSelectedLayer().observe(mapFragment.getViewLifecycleOwner(), layerStructure -> {
            if (layerStructure != null)
            {
                currentLayer = layerStructure.getProperty();
                index = layerStructure.getIndex();
                unit = layerStructure.getUnit();
            }
            else
                currentLayer = "";
            Log.d("current layer:", currentLayer);
            mMap.clear();
            addWmsLayer(currentLayer);
        });
        }

    public void openBasemapSelection(View view) {
        BasemapSelectionFragment basemapSelectionFragment = new BasemapSelectionFragment();
        basemapSelectionFragment.show(getSupportFragmentManager(), "basemapSelectionFragment");

        layerViewModel.getBasemap().observe(mapFragment.getViewLifecycleOwner(), basemap -> {
            if (basemap != null)
            {
                maptype = basemap;
                mMap.setMapType(maptype);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(maptype);

        // Add WMS Layer
        addWmsLayer(currentLayer);

        // Move the camera to a default location
        // Add a marker and move the camera
        LatLng hanoi = new LatLng(21.033333, 105.849998);
        LatLng thaibinh = new LatLng(20.45, 106.34002);
//        mMap.addMarker(new MarkerOptions().position(hanoi).title("Marker in Thaibinh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thaibinh, 5));

        mMap.setOnMapClickListener(point -> {
            tapPoint = point;
            mapFragment.getView().setClickable(false);
            onMapClick(tapPoint);

            new Handler(Looper.getMainLooper()).post(() -> mapFragment.getView().setClickable(true));
        });
        mMap.setOnCameraMoveListener(() -> {
            if (tapPoint != null)
                updateTapTextPosition(tapPoint);
        });

    }

    @Override
        public void onMapClick(LatLng point) {
            String text = "Latitude: " + point.latitude +
                    "\n" + "Longitude: " + point.longitude;

            tapTextView.setText(text);
            updateTapTextPosition(point);

            if (currentLayer != null && !currentLayer.isEmpty())
            {
//                String[] temp = currentLayer.split("_");
//                if (temp.length > 0)
//                {
//                    layerRepository.fetchInfo(point.longitude, point.latitude, temp[0], temp[1]).observe(this, s -> {
//                        tapTextView.setText(text + "\n" + getFullName(temp[0]) + ": " + s);
//                        updateTapTextPosition(point);
//                    });
//                }
                int zoom = (int) mMap.getCameraPosition().zoom;
                LatLngBounds bounds = getLatLngBoundsForPoint(point, zoom);
                String bbox = bounds.southwest.longitude + "," + bounds.southwest.latitude + "," +
                        bounds.northeast.longitude + "," + bounds.northeast.latitude;
                layerRepository.fetchInfo(currentLayer, bbox).observe(this, s -> {
                    if (!s.equals("no info"))
                        tapTextView.setText(text + "\n" + index + ": " + s + " " + unit);
                    else
                        tapTextView.setText(text + "\n" + s);
                    updateTapTextPosition(point);
                });
            }

            tapTextView.setVisibility(View.VISIBLE);
        }

    private LatLngBounds getLatLngBoundsForPoint(LatLng latLng, int zoom) {
        // Google Maps tile size
        int TILE_SIZE = 256;

        // Earth's circumference in meters (approx.)
        double EARTH_CIRCUMFERENCE = 40075017;

        // Calculate meters per pixel at the given zoom level
        double metersPerPixel = EARTH_CIRCUMFERENCE / (TILE_SIZE * Math.pow(2, zoom));

        // Approximate size of a tile in degrees
        double latDegreeSpan = (metersPerPixel / EARTH_CIRCUMFERENCE) * 360;
        double lngDegreeSpan = latDegreeSpan / Math.cos(Math.toRadians(latLng.latitude));

        // Calculate bounds
        double south = latLng.latitude - latDegreeSpan / 2;
        double north = latLng.latitude + latDegreeSpan / 2;
        double west = latLng.longitude - lngDegreeSpan / 2;
        double east = latLng.longitude + lngDegreeSpan / 2;

        return new LatLngBounds(new LatLng(south, west), new LatLng(north, east));
    }


    private void updateTapTextPosition(LatLng point){
        Projection projection = mMap.getProjection();
        Point screenPoint = projection.toScreenLocation(point);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        tapTextView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int viewWidth = tapTextView.getMeasuredWidth();
        int viewHeight = tapTextView.getMeasuredHeight();

        int x = screenPoint.x - viewWidth / 2;
        if (x < 0)
            x = 0;
        if (x > width - viewWidth)
            x = width - viewWidth;
        int y = screenPoint.y - viewHeight;
        if (y < 0)
            y = 0;
        if (y > height - viewHeight)
            y = height - viewHeight;

        tapTextView.setTranslationX(x);
        tapTextView.setTranslationY(y);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        tapTextView.setText("long pressed, point=" + point);
    }

    private void addWmsLayer(String layer) {
        if (layer.isEmpty())
        {
            if (tileOverlay != null)
            {
                tileOverlay.remove();
            }

            return;
        }

        TileProvider tileProvider = new UrlTileProvider(256, 256) {

            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                LatLngBounds bounds = getTileBounds(x, y, zoom);

                String tileUrl = "https://geoserver.geoscience.vn:8443/geoserver/soil/wms?service=WMS&version=1.1.0&request=GetMap" +
                        "&layers=soil:" + layer +
                        "&bbox=" + bounds.southwest.longitude + "," + bounds.southwest.latitude + "," +
                        bounds.northeast.longitude + "," + bounds.northeast.latitude +
                        "&width=256&height=256" +
                        "&srs=EPSG:4326&styles=&format=image/png";

                    Log.d("tileURL", tileUrl);
                    Log.d("tile", x + ", " + y + ", " + zoom);

                    try {
                        return new URL(tileUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Log.e("TileURL", "Malformed URL: " + tileUrl, e);
                        return null;
                    }
                };
        };

        if (tileOverlay != null)
        {
            tileOverlay.remove();
            tileOverlay.clearTileCache();
        }

        // Add the tile overlay to the map
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(tileProvider);
        if (tileOverlayOptions != null)
            tileOverlay = mMap.addTileOverlay(tileOverlayOptions);
    }

    private static final double RADIUS = 6378137; // Earth's radius in meters

    // This function calculates the bounding box for a tile
    // (simple version, may need improvements based on actual tile service projection)
    private LatLngBounds getTileBounds(int x, int y, int zoom) {
        // Total number of tiles at this zoom level
        int totalTiles = 1 << zoom; // Equivalent to 2^zoom

        // Calculate longitude
        double lonLeft = x / (double) totalTiles * 360.0 - 180.0;
        double lonRight = (x + 1) / (double) totalTiles * 360.0 - 180.0;

        // Calculate latitude
        double latTop = mercatorToLatitude(Math.PI - 2.0 * Math.PI * y / totalTiles);
        double latBottom = mercatorToLatitude(Math.PI - 2.0 * Math.PI * (y + 1) / totalTiles);

        // Create a LatLngBounds object
        LatLng southwest = new LatLng(latBottom, lonLeft); // Bottom-left
        LatLng northeast = new LatLng(latTop, lonRight);   // Top-right
        return new LatLngBounds(southwest, northeast);
    }

    private double mercatorToLatitude(double mercatorY) {
        return Math.toDegrees(Math.atan(Math.sinh(mercatorY)));
    }

    public static double[] tileToLonLat(int x, int y, int zoom) {
        // Convert tile x to longitude
        double lon = x / Math.pow(2.0, zoom) * 360.0 - 180.0;

        // Convert tile y to latitude
        double n = Math.PI - 2.0 * Math.PI * y / Math.pow(2.0, zoom);
        double lat = Math.toDegrees(Math.atan(Math.sinh(n)));

        return new double[]{lat, lon};
    }

    public static double[][] tileBounds(int x, int y, int zoom) {
        // Top-left corner
        double[] topLeft = tileToLonLat(x, y, zoom);
        // Bottom-right corner
        double[] bottomRight = tileToLonLat(x + 1, y + 1, zoom);

        return new double[][]{topLeft, bottomRight};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle the new intent, e.g., update UI with new data
        setIntent(intent);

        tapTextView.setVisibility(View.GONE);

        boolean changed = false;

        // Handle new maptype
        int tempMap = intent.getIntExtra("basemap", maptype);
        if (tempMap != maptype)
        {
            maptype = tempMap;
            mMap.setMapType(maptype);
        }

        // Handle new layer
        String temp = intent.getStringExtra("current layer");
        if (temp != null) {
            if (!temp.equals(currentLayer)) {
                currentLayer = temp;
                addWmsLayer(currentLayer);
                Log.d("current layer", currentLayer);
            }
        }

        // Handle new unit
        unit = intent.getStringExtra("unit");
    }

}