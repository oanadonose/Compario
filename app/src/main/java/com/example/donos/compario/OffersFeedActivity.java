package com.example.donos.compario;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.Offer;
import models.User;

public class OffersFeedActivity extends AppCompatActivity {
    //shared preferences
    public SharedPreferences userSettings;
    public SharedPreferences.Editor editor;
    DBManager dbManager;
    SQLiteDatabase sqlDB;

    private ArrayList<Offer> shoppingList;
    //TODO: MAKE ACTIVITY EXTEND LISTVIEW? -- https://developer.android.com/guide/topics/search/search-dialog //performing a search
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userReference;
    private DatabaseReference offersReference;
    private DatabaseReference shoppingListReference;
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
                    //current activity
                    //FirebaseAuth.getInstance().signOut();
                    //Intent appIntent = new Intent(OffersFeedActivity.this,OffersFeedActivity.class);
                    //OffersFeedActivity.this.startActivity(appIntent);
                    //finish();
                    return true;
                    // finish();

                }
                case R.id.navigation_compare: {
                    //FirebaseAuth.getInstance().signOut();
                    Intent appIntent = new Intent(OffersFeedActivity.this, ShoppingListActivity.class);
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
    //adapter and listview
    private ListView listView;

    private Button searchEnter;
    private EditText searchQuery;
    private Integer clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_feed);

        dbManager = new DBManager(this);

        clickCount = 0;

        searchEnter = findViewById(R.id.searchEnter);
        searchQuery = findViewById(R.id.searchText);

        //fab
        FloatingActionButton searchBtn = findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount = clickCount + 1;
                //make search bar visible on click and invisible if you click again
                if(clickCount % 2 != 0 )
                {
                    searchQuery.setVisibility(View.VISIBLE);
                    searchEnter.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchQuery.setVisibility(View.INVISIBLE);
                    searchEnter.setVisibility(View.INVISIBLE);
                }

            }
        });

        //listview
        listView = findViewById(R.id.offerList);


        //Navigation view
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        navigation.setSelectedItemId(R.id.navigation_nearby);//set selected item!!
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        userSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        editor = userSettings.edit();

        final String userCountry = userSettings.getString("country", "Country");
        final String userCity = userSettings.getString("city", "City");
        Toast.makeText(this, "found current country: " + userCountry + "\nand current city: " + userCity, Toast.LENGTH_LONG).show();

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                } else {
                    // No user is signed in
                    Intent noUser = new Intent(OffersFeedActivity.this, LoginActivity.class);
                    OffersFeedActivity.this.startActivity(noUser);
                }

            }
        });
        uid = user.getUid();

        userReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get user object
                currentUser = dataSnapshot.getValue(User.class);
                country = currentUser.country;
                city = currentUser.city;

                offersReference = FirebaseDatabase.getInstance().getReference().child("offers").child(country).child(city);
                ValueEventListener offerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final ArrayList<Offer> offers = new ArrayList<>();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            String storeName = childDataSnapshot.child("store_name").getValue(String.class);
                            final String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
                            String price = childDataSnapshot.child("price").getValue(String.class);
                            String category = childDataSnapshot.child("category").getValue(String.class);
                            Offer offer = new Offer(storeName, offerTitle, price, category);

                            //add this to custom array adapter
                            offers.add(offer);
                            //create adapter
                            OfferAdapter adapter = new OfferAdapter(OffersFeedActivity.this, offers);
                            listView.setAdapter(adapter);


                            searchEnter = findViewById(R.id.searchEnter);
                            searchEnter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String query = searchQuery.getText().toString();
                                    ArrayList<Offer> searchResults = new ArrayList<>();
                                    for(Offer offer: offers)
                                    {
                                        if(query.equals(""))
                                        {
                                            //if search is empty but button pressed, don't change original array
                                        }

                                        //make query string and offer info lowercase so you get expected results
                                        String offerTitleLwr = offer.offerTitle.toLowerCase();
                                        String storeNameLwr = offer.storeName.toLowerCase();
                                        String categoryLwr = offer.category.toLowerCase();
                                        String queryLwr = query.toLowerCase();

                                        //if at least 1 result is found
                                        if(offerTitleLwr.contains(queryLwr)||storeNameLwr.contains(queryLwr)||categoryLwr.contains(queryLwr))
                                        {
                                            searchResults.add(offer);
                                            OfferAdapter adapterSearch = new OfferAdapter(OffersFeedActivity.this,searchResults);
                                            listView.setAdapter(adapterSearch);
                                        }

                                        //if search does not return anything
                                        else
                                        {
                                            Toast.makeText(OffersFeedActivity.this,"No results for search word: "+query,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userReference.addValueEventListener(userListener);
        final ArrayList<Offer> shoppingList = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Offer selectedItem = (Offer) parent.getItemAtPosition(position);
                String[] projection = {"OfferTitle","Store","Price","Category"};
                final String selection = "OfferTitle=? AND Store=? AND Price=? AND Category=?";
                String[] selectionArgs = {selectedItem.offerTitle, selectedItem.storeName, selectedItem.price, selectedItem.category};
                Cursor cursor = dbManager.query(projection,selection,selectionArgs,DBManager.ColTitle);
                if(cursor.getCount()>0)
                {
                    Snackbar.make(findViewById(R.id.coordLay), "Removed "+selectedItem.offerTitle + " from shopping list.",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    //update dbs

                    //delete from sqlite local storage
                    dbManager.delete(selection,selectionArgs);
                    //delete from firebase
                    deleteItem(selectedItem,uid);
                }
                else if(cursor.getCount()==0)
                {
                    //TODO: make snackbar undo
                    Snackbar.make(findViewById(R.id.coordLay),"Added "+selectedItem.offerTitle + " to shopping list.",Snackbar.LENGTH_SHORT).show();
                    shoppingList.add(selectedItem);
                    writeNewOffer(selectedItem,uid);

                    //add to local storage
                    ContentValues values = new ContentValues();
                    String offerTitleFav = selectedItem.offerTitle;
                    String storeNameFav = selectedItem.storeName;
                    String categoryFav = selectedItem.category;
                    String priceFav = selectedItem.price;
                    values.put(DBManager.ColTitle,offerTitleFav);
                    values.put(DBManager.ColStore,storeNameFav);
                    values.put(DBManager.ColCategory,categoryFav);
                    values.put(DBManager.ColPrice,priceFav);
                    dbManager.Insert(values);
                }
            }
        });

    }//on create end
    private void deleteItem(final Offer selectedItem,String uid){
        shoppingListReference = FirebaseDatabase.getInstance().getReference().child("shopping_list").child(uid);
        ValueEventListener shopListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String storeName = childDataSnapshot.child("store_name").getValue(String.class);
                    String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
                    String category = childDataSnapshot.child("category").getValue(String.class);
                    String price = childDataSnapshot.child("price").getValue(String.class);

                    if(offerTitle!=null){
                        if(offerTitle.equals(selectedItem.offerTitle)&&storeName.equals(selectedItem.storeName)&&category.equals(selectedItem.category)
                                &&price.equals(selectedItem.price))
                        {
                            childDataSnapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        shoppingListReference.addValueEventListener(shopListener);
    }
    private void writeNewOffer(Offer offer, String uid) {
        String key = mDatabase.child("shopping_list").child(uid).push().getKey();
        Offer offerObj = new Offer(offer.storeName,offer.offerTitle,offer.price,offer.category);
        Map<String, Object> offerValues = offer.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/shopping_list/"+uid+"/"+key,offerValues);
        mDatabase.updateChildren(childUpdates);
    }
}//class end
