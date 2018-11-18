package com.example.donos.compario;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class UserAreaActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    public SharedPreferences locSettings;
    public SharedPreferences.Editor editor;

    //the default location for the map center
    final String defaultLatitudeString = "52.407469";
    final double defaultLatitude = Double.parseDouble(defaultLatitudeString);
    final String defaultLongitudeString = "-1.503459";
    final double defaultLongitude = Double.parseDouble(defaultLongitudeString);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //Create the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //must calculate the shops array from db and initialize
        String[] shopArray = {"Morrisons","B&Q","Lidl","Londis","Tesco","ASDA","Waterstones","Debenhams"};
        //Create list adapter
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview,R.id.shopName, shopArray);
        //Create the list
        ListView shopList = (ListView) findViewById(R.id.shopslist);
        shopList.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        final SharedPreferences locSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = locSettings.edit();
        if(locSettings.contains("last_lat") && locSettings.contains("last_long")){
            String latitudeString = locSettings.getString("last_lat",defaultLatitudeString);
            String longitudeString = locSettings.getString("last_long",defaultLongitudeString);

            double userLatitude = Double.parseDouble(latitudeString);
            double userLongitude = Double.parseDouble(longitudeString);

            //centers on user location
            CameraPosition userLocation = new CameraPosition.Builder()
                    .target(new LatLng(userLatitude, userLongitude))
                    .zoom(13)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(userLocation));
        }
        else{
            Toast.makeText(this,"Please click on *my location* button located in the top right corner.",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        //Gets user location latitude and longitude
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String latitudeString = Double.toString(latitude);
        String longitudeString = Double.toString(longitude);
        Toast.makeText(this, "lat " + latitudeString+",\n" + longitudeString, Toast.LENGTH_SHORT).show();
        //Adds user location to shared preferences for later use
        final SharedPreferences locSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = locSettings.edit();
        editor.putString("last_lat",latitudeString);
        Log.d("editor:","put last lat string into editor");
        editor.putString("last_long",longitudeString);
        Log.d("editor:","put last long string into editor");
        editor.apply();
        Log.d("editor:","apply into editor");
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}