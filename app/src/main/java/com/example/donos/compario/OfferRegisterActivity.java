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

public class OfferRegisterActivity extends AppCompatActivity {

    private EditText etStoreName;
    private EditText etOfferTitle;
    private EditText etPrice;
    private EditText etCategory;
    private Button offerNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_register);

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }//end code to make status bar and navigation bar transparent

        //Views
        etStoreName = findViewById(R.id.shopOfferName);
        etOfferTitle = findViewById(R.id.offerName);
        etPrice = findViewById(R.id.offerPrice);
        etCategory = findViewById(R.id.offerCategory);
        offerNextButton = findViewById(R.id.offerNextBtn);

        offerNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateForm()){
                    return;
                }
                else{
                    String storeName = etStoreName.getText().toString();
                    String offerTitle = etOfferTitle.getText().toString();
                    String price = etPrice.getText().toString();
                    String category = etCategory.getText().toString();
                    Intent intent = new Intent(OfferRegisterActivity.this, OfferRegister2Activity.class);
                    intent.putExtra("store_name",storeName);
                    intent.putExtra("offer_title",offerTitle);
                    intent.putExtra("offer_price",price);
                    intent.putExtra("offer_category",category);
                    startActivity(intent);
                }
            }
        });

    }//oncreate end
    private boolean validateForm() {
        boolean result = true;
        //store name
        if (TextUtils.isEmpty(etStoreName.getText().toString())) {
            etStoreName.setError("Required");
            result = false;
        } else {
            etStoreName.setError(null);
        }
        //offer title
        if (TextUtils.isEmpty(etOfferTitle.getText().toString())) {
            etOfferTitle.setError("Required");
            result = false;
        } else {
            etOfferTitle.setError(null);
        }
        //offer price
        if (TextUtils.isEmpty(etPrice.getText().toString())) {
            etPrice.setError("Required");
            result = false;
        } else {
            etPrice.setError(null);
        }
        //offer category
        if (TextUtils.isEmpty(etCategory.getText().toString())) {
            etCategory.setError("Required");
            result = false;
        } else {
            etCategory.setError(null);
        }
        return result;
    }
}//class end
