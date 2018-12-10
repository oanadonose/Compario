package com.example.donos.compario;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import models.Offer;
import models.User;

public class ShoppingListActivity extends AppCompatActivity {

    private DBManager dbManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userReference;
    private DatabaseReference offersReference;
    private DatabaseReference shoppingListReference;
    private String uid;

    private User currentUser;
    private String country;
    private String city;

    private ArrayList<Offer> shoppingList;

    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_feed: {
                    Intent appIntent = new Intent(ShoppingListActivity.this, UserAreaActivity.class);
                    ShoppingListActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
                case R.id.navigation_nearby: {

                    //FirebaseAuth.getInstance().signOut();
                    Intent appIntent = new Intent(ShoppingListActivity.this, OffersFeedActivity.class);
                    ShoppingListActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                    // finish();

                }
                case R.id.navigation_compare: {
                    //this is current activity
                    //FirebaseAuth.getInstance().signOut();
                    //Intent appIntent = new Intent(OffersFeedActivity.this, LoginActivity.class);
                    //OffersFeedActivity.this.startActivity(appIntent);
                    //finish();
//                    Intent appIntent = new Intent(UserAreaActivity.this, CompareActivity.class);
//                    UserAreaActivity.this.startActivity(appIntent);
                    return true;
                }
                case R.id.navigation_profile: {
                    Intent appIntent = new Intent(ShoppingListActivity.this, ProfileActivity.class);
                    ShoppingListActivity.this.startActivity(appIntent);
                    finish();
                    return true;
                }
            }
            return false;
        }
    };
    //adapter and listview
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        dbManager = new DBManager(this);

        //Navigation view
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        navigation.setSelectedItemId(R.id.navigation_compare);//set selected item!!
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //listview
        listView = findViewById(R.id.offerList);

        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                } else {
                    // No user is signed in
                    Intent noUser = new Intent(ShoppingListActivity.this, LoginActivity.class);
                    ShoppingListActivity.this.startActivity(noUser);
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
                //country = currentUser.country;
                //city = currentUser.city;

                offersReference = FirebaseDatabase.getInstance().getReference().child("shopping_list").child(uid);
                ValueEventListener offerListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Offer> offers = new ArrayList<>();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            String storeName = childDataSnapshot.child("store_name").getValue(String.class);
                            String offerTitle = childDataSnapshot.child("offer_title").getValue(String.class);
                            String price = childDataSnapshot.child("price").getValue(String.class);
                            String category = childDataSnapshot.child("category").getValue(String.class);
                            Offer offer = new Offer(storeName, offerTitle, price, category);
                            //add this to custom array adapter
                            offers.add(offer);
                            //create adapter
                            OfferAdapter adapter = new OfferAdapter(ShoppingListActivity.this, offers);
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Offer selectedItem = (Offer) parent.getItemAtPosition(position);

                String[] projection = {"OfferTitle","Store","Price","Category"};
                final String selection = "OfferTitle=? AND Store=? AND Price=? AND Category=?";
                String[] selectionArgs = {selectedItem.offerTitle, selectedItem.storeName, selectedItem.price, selectedItem.category};
                Cursor cursor = dbManager.query(projection,selection,selectionArgs,DBManager.ColTitle);
                if(cursor.getCount()>0)
                {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Removed "+selectedItem.offerTitle + " from shopping list.",
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //update dbs

                    //delete from sqlite local storage
                    dbManager.delete(selection,selectionArgs);
                    //delete from firebase
                    deleteItem(selectedItem,uid);
                }
            }
        }); //TODO:remove item from list on click or restore it.
        }//end on create
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
    }//end class

