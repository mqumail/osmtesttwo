package com.example.osmtesttwo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import static com.example.osmtesttwo.Utils.getBitmapFromVectorDrawable;

public class MainActivity extends AppCompatActivity {

    // TODO: Put in a general file
    private static final int COARSE_LOCATION_PERMISSION_CODE = 100;
    private static final int FINE_LOCATION_PERMISSION_CODE = 101;
    private static final int WIFI_STATE_PERMISSION_CODE = 102;
    private static final int NETWORK_STATE_PERMISSION_CODE = 103;
    private static final int INTERNET_PERMISSION_CODE = 104;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 105;

    private MapView map;
    private static final String MY_USER_AGENT = BuildConfig.APPLICATION_ID;
    private MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // My location
        GpsMyLocationProvider myLocationProvider = new GpsMyLocationProvider(this);
        myLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(myLocationProvider, map);
        locationOverlay.enableMyLocation();
        map.getOverlays().add(locationOverlay);

        IMapController mapController = map.getController();
        mapController.animateTo(locationOverlay.getMyLocation(), 0.5, 15L);
        mapController.setZoom(9);
        mapController.setCenter(locationOverlay.getMyLocation());
        map.invalidate();

        // tutorial_1
        //RoadManager roadManager = new OSRMRoadManager(this, MY_USER_AGENT);

//        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//        GeoPoint endPoint = new GeoPoint(48.4, -1.9);
//        waypoints.add(startPoint);
//        waypoints.add(endPoint);

        //Set mean of direction before getting the road
//        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE);
//        Road road = roadManager.getRoad(waypoints);
//
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

//        map.getOverlays().add(roadOverlay);
//
//        Drawable nodeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
//        for (int i=0; i<road.mNodes.size(); i++){
//            RoadNode node = road.mNodes.get(i);
//            Marker nodeMarker = new Marker(map);
//            nodeMarker.setPosition(node.mLocation);
//            nodeMarker.setIcon(nodeIcon);
//            nodeMarker.setTitle("Step "+i);
//            nodeMarker.setSnippet(node.mInstructions);
//            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
//            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_continue, null);
//            nodeMarker.setImage(icon);
//
//            map.getOverlays().add(nodeMarker);
//        }
//
//        map.invalidate();

        // tutorial_2: OpenStreetMap POIs with Nominatim
        //BoundingBox boundingBox = BoundingBox.fromGeoPoints(waypoints);

        //GeoNames
        //GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("mqumail");

        //Nominatim4

//        // Nominatim
//        NominatimPOIProvider poiProvider = new NominatimPOIProvider(MY_USER_AGENT);
//        ArrayList<POI> pois = poiProvider.getPOIInside(boundingBox, "bakery", 50);

        // tutorial_3: Marker Clustering
        //FolderOverlay poiMarkers = new FolderOverlay(this);

        // Weimar BoundingBox
        BoundingBox weimarBoundingBox = new BoundingBox();
        weimarBoundingBox.set(50.9846195739, 11.3378999626, 50.9768111623, 11.3182447349);

        // Code to get the search bar
        EditText searchView = (EditText) findViewById(R.id.searchBar);
        Button searchButton = findViewById(R.id.buttonSearch);

        searchButton.setOnClickListener(view -> {
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE);
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE);
            checkPermission(Manifest.permission.ACCESS_WIFI_STATE, WIFI_STATE_PERMISSION_CODE);
            checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, NETWORK_STATE_PERMISSION_CODE);
            checkPermission(Manifest.permission.INTERNET, INTERNET_PERMISSION_CODE);
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);

            String searchValue = searchView.getText().toString();

            OverpassAPIProvider overpassAPIProvider = new OverpassAPIProvider();
            String url = overpassAPIProvider.urlForPOISearch("amenity=bar", weimarBoundingBox, 50, 15);

            // Get POIs
            ArrayList<POI> POIs = overpassAPIProvider.getPOIsFromUrl(url);

            RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(MainActivity.this);
            Bitmap clusterIcon = getBitmapFromVectorDrawable(MainActivity.this, R.drawable.marker_cluster);
            poiMarkers.setIcon(clusterIcon);

            // CLuster Design
            poiMarkers.getTextPaint().setColor(Color.DKGRAY);
            poiMarkers.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density); //taking into account the screen density
            poiMarkers.mAnchorU = Marker.ANCHOR_RIGHT;
            poiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
            poiMarkers.mTextAnchorV = 0.40f;

            map.getOverlays().add(poiMarkers);

            //Drop Pins
            Drawable poiIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_poi_default, null);
            for (POI poi : POIs){
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(poiIcon);
                if (poi.mThumbnail != null){
                    poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                }
                poiMarker.setInfoWindow(new CustomInfoWindow(map));
                poiMarker.setRelatedObject(poi);
                poiMarkers.add(poiMarker);
            }
            map.invalidate();
        });
    }

    private void init()
    {
        //Allow to run on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Set user agent
        final Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(MY_USER_AGENT);
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Coarse Location Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Coarse Location Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "FINE LOCATION Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "FINE LOCATION Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == WIFI_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "WIFI STATE Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "WIFI STATE Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == NETWORK_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "NETWORK_STATE Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "NETWORK_STATE Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "INTERNET_PERMISSION Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "INTERNET_PERMISSION Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Storage Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


}