package com.example.daniel.neighbourhoodwatch;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.List;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener,
        LocationEngineListener, PermissionsListener{

    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private Location originLocation;
    private Point destinationPoint;
    private MapboxMap map;
    private LocationLayerPlugin locationLayerPlugin;
    private LatLng originCoord;
    private NavigationMapRoute navigationMapRoute;
    private DirectionsRoute currentRoute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Mapbox.getInstance(this, getString(R.string.access_token));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = this.getIntent().getExtras();
        String destination = null;
        if(extras != null){
            destination = extras.getString("LngLat");

            String[] separated = destination.split(",");
           destinationPoint = Point.fromLngLat(Double.parseDouble(separated[0]),Double.parseDouble(separated[1]));

           //create this method to trigger on Load
           //setRoute(destinationPoint, originLocation);

        }

        mapView.onCreate(savedInstanceState);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mapView.getMapAsync(this);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.activateLocationComponent(this);
        originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());


    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mapView, map);
            // Set the plugin's camera mode
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            getLifecycle().addObserver(locationLayerPlugin);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"We need your permission to use the map features", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (!granted) {
            Toast.makeText(this, "You did not grant permission", Toast.LENGTH_LONG).show();
            finish();
            permissionsManager.requestLocationPermissions(this);
        } else{
            enableLocationPlugin();
        }
    }



    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            originLocation = location;
            setCameraPosition(location);
        }
    }

    private void setCameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13.0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

    }

    public void onPanic(View view){

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void setRoute(Point destination, LatLng origin){

        Point originPoint = Point.fromLngLat(origin.getLongitude(),origin.getLatitude());

        if (destination != null) {
            NavigationRoute.builder(this)
                    .accessToken(Mapbox.getAccessToken())
                    .origin(originPoint)
                    .destination(destination)
                    .build().getRoute(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    // You can get the generic HTTP info about the response
                    Timber.d("Response code: %s", response.code());
                    if (response.body() == null) {
                        Timber.e("No routes found, make sure you set the right Route and access token.");
                        return;
                    } else if (response.body().routes().size() < 1) {
                        Timber.e("No routes found");
                        return;
                    }
                    currentRoute = response.body().routes().get(0);
                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute.removeRoute();
                    } else {
                        navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                    }
                    navigationMapRoute.addRoute(currentRoute);

                    if(currentRoute != null){
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(true)
                                .enableOffRouteDetection(true)
                                .build();
                    }

                    //navigationButton.setOnClickListener(v -> { NavigationLauncher.startNavigation(MainActivity.this,options);}

                    }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    Timber.e("Error: %s", t.getMessage());
                }
            });
        }
    }



}
