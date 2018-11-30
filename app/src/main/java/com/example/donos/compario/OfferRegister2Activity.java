package com.example.donos.compario;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import models.Address;
import models.Offer;
import models.Store;

public class OfferRegister2Activity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private EditText etOfferCity;
    private EditText etOfferCountry;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_register2);

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        } //end code to make status bar and navigation bar transparent

        //get extras from previous activity bundle
        Bundle bundle = getIntent().getExtras();
        final String storeName = bundle.getString("store_name");
        final String offerTitle =  bundle.getString("offer_title");
        final String price = bundle.getString("offer_price");
        final String category = bundle.getString("offer_category");

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //Views
        etOfferCity = findViewById(R.id.offerCity);
        etOfferCountry = findViewById(R.id.offerCountry);
        finishButton = findViewById(R.id.offerFinishButton);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                else{
                    final String offerCity = etOfferCity.getText().toString();
                    final String offerCountry = etOfferCountry.getText().toString();
                    Offer offer = new Offer(storeName,offerTitle,price,category,offerCity,offerCountry);
                    writeNewOffer(offer);
                    Intent intent = new Intent(OfferRegister2Activity.this, UserAreaActivity.class);
                    startActivity(intent);
                }
            }
        });
    }//oncreate end
    private void writeNewOffer(Offer offer) {
        String key = mDatabase.child("offers").child(offer.country).child(offer.city).push().getKey();
        Offer offerObj = new Offer(offer.storeName,offer.offerTitle,offer.price,offer.category);
        Map<String, Object> offerValues = offer.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/offers/"+offer.country+"/"+offer.city+"/"+key,offerValues);
        mDatabase.updateChildren(childUpdates);
        //mDatabase.child("stores").child(addressFull.country).child(addressFull.city).setValue(store);
    }
    private boolean validateForm() {
        boolean result = true;
        //COUNTRY
        if (TextUtils.isEmpty(etOfferCountry.getText().toString())) {
            etOfferCountry.setError("Required");
            result = false;
        } else {
            etOfferCountry.setError(null);
        }
        //CITY
        if (TextUtils.isEmpty(etOfferCity.getText().toString())) {
            etOfferCity.setError("Required");
            result = false;
        } else {
            etOfferCity.setError(null);
        }
        return result;
    }
}//class end
