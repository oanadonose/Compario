package com.example.donos.compario;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.Offer;
import models.User;

public class OffersFeedActivity extends AppCompatActivity {
//TODO: MAKE ACTIVITY EXTEND LISTVIEW? -- https://developer.android.com/guide/topics/search/search-dialog //performing a search
//TODO: MAKE FAB FOR SHOPPING LIST HERE AND IN COMPARE TAB
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private DatabaseReference offersReference;
    private String uid;
    private User currentUser;
    private String country;
    private String city;

    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_feed: {
                    //DONT DO ANYTHING THIS IS CURRENT ACTIVITY.
                    Intent appIntent = new Intent(OffersFeedActivity.this, UserAreaActivity.class);
                    OffersFeedActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_nearby: {

                    //FirebaseAuth.getInstance().signOut();
                    //Intent appIntent = new Intent(OffersFeedActivity.this,OffersFeedActivity.class);
                    //OffersFeedActivity.this.startActivity(appIntent);
                    //finish();
                    return true;
                    // finish();

                }
                case R.id.navigation_compare: {
                    FirebaseAuth.getInstance().signOut();
                    Intent appIntent = new Intent(OffersFeedActivity.this, LoginActivity.class);
                    OffersFeedActivity.this.startActivity(appIntent);
                    finish();
//                    Intent appIntent = new Intent(UserAreaActivity.this, CompareActivity.class);
//                    UserAreaActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_profile: {
                    Intent appIntent = new Intent(OffersFeedActivity.this, ProfileActivity.class);
                    OffersFeedActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
            }
            return false;
        }
    };
    //shared preferences
    public SharedPreferences userSettings;
    public SharedPreferences.Editor editor;

    //adapter and listview
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_feed);

        //Navigation view
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        navigation.setSelectedItemId(R.id.navigation_nearby);//set selected item!!
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //searchable
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
        }

        //fab
        FloatingActionButton searchBtn = findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });

        //listview
        listView = (ListView) findViewById(R.id.offerList);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user!=null)
                {
                }
                else
                {
                    // No user is signed in
                    Intent noUser = new Intent(OffersFeedActivity.this,LoginActivity.class);
                    OffersFeedActivity.this.startActivity(noUser);
                }

            }
        });
        uid = user.getUid();

        //Adds user location to shared preferences for later use
        userSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        editor = userSettings.edit();

        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get user object
                currentUser = dataSnapshot.getValue(User.class);
                country = currentUser.country;
                city = currentUser.city;
                //editor.putString("country",country);
                //editor.putString("city",city);
                offersReference = FirebaseDatabase.getInstance().getReference().child("offers").child(country).child(city);
                ValueEventListener offerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Offer> offers = new ArrayList<>();
                        for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                            String storeName = childDataSnapshot.child("store_name").getValue(String.class);
                            String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
                            String price = childDataSnapshot.child("price").getValue(String.class);
                            String category = childDataSnapshot.child("category").getValue(String.class);
                            Offer offer = new Offer(storeName,offerTitle,price,category);
                            //add this to custom array adapter
                            offers.add(offer);
                            //create adapter
                            OfferAdapter adapter = new OfferAdapter(OffersFeedActivity.this,offers);
                            listView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                offersReference.addValueEventListener(offerListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userReference.addValueEventListener(userListener);




        //final String userCountry = userSettings.getString("country","Country");
        //final String userCity = userSettings.getString("city", "City");

        //MAKE LIST DISPLAY ALL ITEMS IN CITY
//        offersReference = FirebaseDatabase.getInstance().getReference().child("offers").child(userCountry).child(userCity);
//        ValueEventListener offerListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<Offer> offers = new ArrayList<>();
//                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
//                    String storeName = childDataSnapshot.child("store_name").getValue(String.class);
//                    String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
//                    String price = childDataSnapshot.child("price").getValue(String.class);
//                    String category = childDataSnapshot.child("category").getValue(String.class);
//                    Offer offer = new Offer(storeName,offerTitle,price,category);
//                    //add this to custom array adapter
//                    offers.add(offer);
//                    //create adapter
//                    OfferAdapter adapter = new OfferAdapter(OffersFeedActivity.this,offers);
//                    listView.setAdapter(adapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        offersReference.addValueEventListener(offerListener);
    }//on create end
}//class end
