package com.example.donos.compario;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import models.Address;
import models.Store;

public class ShopRegisterAddressActivity extends AppCompatActivity {
    private static final String TAG = "ShopAddressActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;

    private EditText etStreet;
    private EditText etStreetNo;
    private EditText etCity;
    private EditText etCountry;
    private EditText etPostcode;
    private Button finishButton;

    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register_address);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("store_name");
        final String email =  bundle.getString("store_email");
        final String phone = bundle.getString("store_phone");
        final Double latitude = bundle.getDouble("store_lat");
        final Double longitude = bundle.getDouble("store_long");

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "user is diff to null");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d(TAG, "getting current userid");
            userID = user.getUid();
            //Log.d(TAG,userID); //works!!!!
        } else if (userID == null) {
            Log.d(TAG, "start login activity");
            Intent appIntent = new Intent(this, LoginActivity.class);
            startActivity(appIntent);
        }


        //Views
        etStreet =  findViewById(R.id.shopStreet);
        etStreetNo =  findViewById(R.id.shopStreetNo);
        etCity = findViewById(R.id.shopCity);
        etCountry =  findViewById(R.id.shopCountry);
        etPostcode =  findViewById(R.id.shopPostcode);
        finishButton = findViewById(R.id.finishButton);



        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                } else {
                    final String street = etStreet.getText().toString();
                    final String streetNo = etStreetNo.getText().toString();
                    final String city = etCity.getText().toString();
                    final String country = etCountry.getText().toString();
                    final String postcode = etPostcode.getText().toString();
                    Address addressFull = new Address(street, streetNo, city, country, postcode, latitude, longitude);
                    Address addressDB = new Address(street,streetNo,postcode,latitude,longitude);
                    writeNewStore(name, email, phone, addressFull,addressDB);
                    Intent intent = new Intent(ShopRegisterAddressActivity.this, UserAreaActivity.class);
                    startActivity(intent);
                }
            }
        });
    }//oncreate end
    private void writeNewStore(String name, String email, String phone, Address addressFull, Address addressDB) {
        String key = mDatabase.child("stores").child(addressFull.country).child(addressFull.city).push().getKey();
        Store store = new Store(name, email, phone,addressDB);
        Map<String, Object> storeValues = store.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/stores/"+addressFull.country+"/"+addressFull.city+"/"+key,storeValues);
        mDatabase.updateChildren(childUpdates);
       //mDatabase.child("stores").child(addressFull.country).child(addressFull.city).setValue(store);
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etStreet.getText().toString())) {
            etStreet.setError("Required");
            result = false;
        } else {
            etStreet.setError(null);
        }
        if (TextUtils.isEmpty(etStreetNo.getText().toString())) {
            etStreetNo.setError("Required");
            result = false;
        } else {
            etStreetNo.setError(null);
        }
        if (TextUtils.isEmpty(etCity.getText().toString())) {
            etCity.setError("Required");
            result = false;
        } else {
            etCity.setError(null);
        }
        if (TextUtils.isEmpty(etCountry.getText().toString())) {
            etCountry.setError("Required");
            result = false;
        } else {
            etCountry.setError(null);
        }
        if (TextUtils.isEmpty(etPostcode.getText().toString())) {
            etPostcode.setError("Required");
            result = false;
        } else {
            etPostcode.setError(null);
        }
        return result;
    }
}//class end
