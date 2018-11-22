package com.example.donos.compario;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;


public class ShopRegisterMapActivity extends AppCompatActivity
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
    //the default location for the map center
    final String defaultLatitudeString = "52.407469";
    final Double defaultLatitude = Double.parseDouble(defaultLatitudeString);
    final String defaultLongitudeString = "-1.503459";
    final Double defaultLongitude = Double.parseDouble(defaultLongitudeString);
    public SharedPreferences locSettings;
    public SharedPreferences.Editor editor;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private LatLng storeLocation;
    private TextView etPlease;
    private String please;
    private TextView edit;
    private String add;
    private Button nextButton;
    private Double latitude;
    private Double longitude;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_feed: {
                    Intent appIntent = new Intent(ShopRegisterMapActivity.this, ShopRegisterMapActivity.class);
                    ShopRegisterMapActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_nearby: {
                    FirebaseAuth.getInstance().signOut();
                    Intent appIntent = new Intent(ShopRegisterMapActivity.this, LoginActivity.class);
                    ShopRegisterMapActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
                case R.id.navigation_compare: {
//                    Intent appIntent = new Intent(ShopRegisterMapActivity.this, CompareActivity.class);
//                    ShopRegisterMapActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_profile: {
                    Intent appIntent = new Intent(ShopRegisterMapActivity.this, ProfileActivity.class);
                    ShopRegisterMapActivity.this.startActivity(appIntent);
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register_map);
        etPlease = findViewById(R.id.etPlease);
        edit = findViewById(R.id.edit);
        nextButton = findViewById(R.id.nextButton);

        Bundle bundle = getIntent().getExtras();
        final String name = bundle.getString("store_name");
        final String email = bundle.getString("store_email");
        final String phone = bundle.getString("store_phone");

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        //Create the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopRegisterMapActivity.this, ShopRegisterAddressActivity.class);
                intent.putExtra("store_name", name);
                intent.putExtra("store_email", email);
                intent.putExtra("store_phone", phone);
                intent.putExtra("store_lat", latitude);
                intent.putExtra("store_long", longitude);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        final SharedPreferences locSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = locSettings.edit();
        if (locSettings.contains("last_lat") && locSettings.contains("last_long")) {
            String latitudeString = locSettings.getString("last_lat", defaultLatitudeString);
            String longitudeString = locSettings.getString("last_long", defaultLongitudeString);

            double userLatitude = Double.parseDouble(latitudeString);
            double userLongitude = Double.parseDouble(longitudeString);

            //centers on user location
            CameraPosition userLocation = new CameraPosition.Builder()
                    .target(new LatLng(userLatitude, userLongitude))
                    .zoom(13)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(userLocation));
        } else {
            Toast.makeText(this, "Please click on *my location* button located in the top right corner.", Toast.LENGTH_LONG).show();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                storeLocation = latLng;
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Store"));
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                Toast.makeText(ShopRegisterMapActivity.this, "latitude: " + latitude.toString() + "\nlongitude: " + longitude.toString(), Toast.LENGTH_LONG).show();
            }
        });
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
        Toast.makeText(this, "lat " + latitudeString + ",\n" + longitudeString, Toast.LENGTH_SHORT).show();
        //Adds user location to shared preferences for later use
        final SharedPreferences locSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = locSettings.edit();
        editor.putString("last_lat", latitudeString);
        Log.d("editor:", "put last lat string into editor");
        editor.putString("last_long", longitudeString);
        Log.d("editor:", "put last long string into editor");
        editor.apply();
        Log.d("editor:", "apply into editor");
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