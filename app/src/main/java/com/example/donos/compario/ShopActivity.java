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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import models.Offer;

public class ShopActivity extends AppCompatActivity {

    String shopName;
    String userCountry;
    String userCity;
    //firebase
    private DatabaseReference mDatabase;

    //bottom nav
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_feed: {
                    Intent appIntent = new Intent(ShopActivity.this, UserAreaActivity.class);
                    ShopActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_nearby: {
                    Intent appIntent = new Intent(ShopActivity.this, ShopActivity.class);
                    ShopActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
                case R.id.navigation_compare: {
                    Intent appIntent = new Intent(ShopActivity.this, ShoppingListActivity.class);
                    ShopActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
                case R.id.navigation_profile: {
                    Intent appIntent = new Intent(ShopActivity.this, ProfileActivity.class);
                    ShopActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
            }
            return false;
        }
    };
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        listView = findViewById(R.id.offerListShop);


        /*
        code from David Passmore on stackoverflow
        https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
         */

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                shopName = null;
                userCity = null;
                userCountry = null;
            } else {
                shopName = extras.getString("shop_name");
                userCity = extras.getString("user_city");
                userCountry = extras.getString("user_country");
            }
        } else {
            shopName = (String) savedInstanceState.getSerializable("shop_name");
            userCity = (String) savedInstanceState.getSerializable("user_city");
            userCountry = (String) savedInstanceState.getSerializable("user_country");
        }

        if (shopName != null && userCountry != null && userCity != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("offers").child(userCountry).child(userCity);
            ValueEventListener storeListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ArrayList<Offer> offers = new ArrayList<>();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        String storeName = childDataSnapshot.child("store_name").getValue().toString();
                        if (storeName.equals(shopName)) {

                            final String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
                            String price = childDataSnapshot.child("price").getValue(String.class);
                            String category = childDataSnapshot.child("category").getValue(String.class);
                            Offer offer = new Offer(storeName, offerTitle, price, category);

                            //add this to custom array adapter
                            offers.add(offer);
                            //create adapter
                            OfferAdapter adapter = new OfferAdapter(ShopActivity.this, offers);
                            listView.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabase.addValueEventListener(storeListener);
        }
    }//oncreate end
}//class end
