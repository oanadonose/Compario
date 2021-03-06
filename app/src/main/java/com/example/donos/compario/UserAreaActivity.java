package com.example.donos.compario;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import models.User;


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
    private ListView shopList;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //the default location for the map center
    final String defaultLatitudeString = "52.407469";
    final double defaultLatitude = Double.parseDouble(defaultLatitudeString);
    final String defaultLongitudeString = "-1.503459";
    final double defaultLongitude = Double.parseDouble(defaultLongitudeString);
    public SharedPreferences locSettings;
    public SharedPreferences.Editor editor;
    String uid;
    String country;
    String city;
    User currentUser;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private String TAG = "customtag";
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private DatabaseReference storeReference;
    private GoogleMap mMap;
    private LatLng storeLocation;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_feed: {
                    //DONT DO ANYTHING THIS IS CURRENT ACTIVITY.
                    //Intent appIntent = new Intent(UserAreaActivity.this, UserAreaActivity.class);
                    //UserAreaActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_nearby: {

                    //FirebaseAuth.getInstance().signOut();
                    Intent appIntent = new Intent(UserAreaActivity.this,OffersFeedActivity.class);
                    UserAreaActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                   // finish();

                }
                case R.id.navigation_compare: {
                    Intent appIntent = new Intent(UserAreaActivity.this, ShoppingListActivity.class);
                    UserAreaActivity.this.startActivity(appIntent);
                    finish();
//                    Intent appIntent = new Intent(UserAreaActivity.this, CompareActivity.class);
//                    UserAreaActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_profile: {
                    Intent appIntent = new Intent(UserAreaActivity.this, ProfileActivity.class);
                    UserAreaActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //Navigation view
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

        //add store fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offerIntent = new Intent(UserAreaActivity.this,OfferRegisterActivity.class);
                UserAreaActivity.this.startActivity(offerIntent);
            }
        });


        //Create the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final ArrayList<String> shops = new ArrayList<String>();

        shopList = findViewById(R.id.shopslist);

        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get user object
                currentUser = dataSnapshot.getValue(User.class);
                country = currentUser.country;
                city = currentUser.city;
                storeReference = FirebaseDatabase.getInstance().getReference().child("stores").child(country).child(city);
                ValueEventListener storeListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "" + childDataSnapshot.getKey()); //displays the key for the node
                            Log.d(TAG, "" + childDataSnapshot.child("name").getValue());
                            String latString = childDataSnapshot.child("address").child("latitude").getValue().toString();
                            String longString = childDataSnapshot.child("address").child("longitude").getValue().toString();
                            Double latitude = Double.parseDouble(latString);
                            Double longitude = Double.parseDouble(longString);
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(childDataSnapshot.child("name").getValue().toString()));
                            shops.add(childDataSnapshot.child("name").getValue().toString());//gives the value for given keyname
                            //Create list adapter
                            ArrayAdapter adapter = new ArrayAdapter<String>(UserAreaActivity.this, R.layout.activity_listview, R.id.shopName, shops);
                            //Create the list

                            shopList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(UserAreaActivity.this, "Failed to load post.",
                                Toast.LENGTH_SHORT).show();
                    }
                };
            storeReference.addValueEventListener(storeListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(UserAreaActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        userReference.addValueEventListener(userListener);

        //onitemclick

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_name = (String) parent.getItemAtPosition(position);
                Intent shopIntent = new Intent(UserAreaActivity.this, ShopActivity.class);
                shopIntent.putExtra("shop_name",shop_name);
                shopIntent.putExtra("user_country",country);
                shopIntent.putExtra("user_city",city);
                UserAreaActivity.this.startActivity(shopIntent);
            }
        });



        //Calculate distance between two points
//        Location locationA = new Location("point A");
//
//        locationA.setLatitude(latA);
//        locationA.setLongitude(lngA);
//
//        Location locationB = new Location("point B");
//
//        locationB.setLatitude(latB);
//        locationB.setLongitude(lngB);
//
//        float distance = locationA.distanceTo(locationB);

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
        //TODO: FIGURE THIS OUT? //storelocation?
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                storeLocation = latLng;
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Store"));
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

}//end class
