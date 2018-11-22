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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShopRegister1Activity extends AppCompatActivity {
    private static final String TAG = "ShopRegister1Activity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText storeName;
    private EditText storeEmail;
    private EditText storePhone;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Views
        storeName = (EditText) findViewById(R.id.shopName);
        storeEmail = (EditText) findViewById(R.id.shopEmail);
        storePhone = (EditText) findViewById(R.id.shopPhone);
        nextButton = (Button) findViewById(R.id.nextBtn);

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateForm()){
                    return;
                }
                else{
                    String name = storeName.getText().toString();
                    String email = storeEmail.getText().toString();
                    String phone = storePhone.getText().toString();
                    Intent intent = new Intent(ShopRegister1Activity.this, ShopRegisterMapActivity.class);
                    intent.putExtra("store_name",name);
                    intent.putExtra("store_email",email);
                    intent.putExtra("store_phone",phone);
                    startActivity(intent);
                }
            }
        });
    } //oncreate end
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(storeName.getText().toString())) {
            storeName.setError("Required");
            result = false;
        } else {
            storeName.setError(null);
        }
        if (TextUtils.isEmpty(storeEmail.getText().toString())) {
            storeEmail.setError("Required");
            result = false;
        } else {
            storeEmail.setError(null);
        }

        return result;
    }
}//class end
