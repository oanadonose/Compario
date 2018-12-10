package com.example.donos.compario;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import models.User;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";

    Context context;

    private String userID;
    private EditText emailProfile;
    private EditText countryProfile;
    private EditText cityProfile;
    private Button finishButton;
    private Button addStoreButton;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //shared pref
    public SharedPreferences userSettings;
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        cityProfile = findViewById(R.id.cityProfile);
        countryProfile = findViewById(R.id.countryProfile);
        emailProfile = findViewById(R.id.emailProfile);
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

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }

        emailProfile = (EditText) findViewById(R.id.emailProfile);
        finishButton = (Button) findViewById(R.id.finishButton);
        addStoreButton = (Button) findViewById(R.id.plusImg);

        final String uid = userID;

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = getResources().getDrawable(R.drawable.tickfull);
                finishButton.setBackground(d);

                //final String currentEmail = emailProfile.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user information
                                User user = dataSnapshot.getValue(User.class);
                                String usernameFromFirebase = user.username;
                                String emailFromFirebase = user.email;
                                String cityFromFirebase = user.city;
                                String countryFromFirebase = user.country;
                                validateForm();
                                if (!validateForm()) {
                                    emailProfile.setText(emailFromFirebase);
                                    cityProfile.setText(cityFromFirebase);
                                    countryProfile.setText(countryFromFirebase);
                                    return;
                                } else if(validateForm()){
                                        final String country = countryProfile.getText().toString();
                                        final String city = cityProfile.getText().toString();
                                        //Create the alert dialog to confirm changes
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                        // Add the buttons
                                        builder.setMessage("Do you want to save changes?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String finishClickEmail = emailProfile.getText().toString();
                                                        //add in shared preferences
                                                        userSettings = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);
                                                        editor = userSettings.edit();
                                                        editor.putString("country",country);
                                                        editor.putString("city",city);
                                                        editor.apply();
                                                        //update the database
                                                        mDatabase.child("users").child(userID).child("email").setValue(finishClickEmail);
                                                        mDatabase.child("users").child(userID).child("country").setValue(country);
                                                        mDatabase.child("users").child(userID).child("city").setValue(city);
                                                        Log.d(TAG, "changed email");
                                                        mDatabase.child("users").child(userID).child("username").setValue(usernameFromEmail(finishClickEmail));
                                                        Intent appIntent = new Intent(ProfileActivity.this, UserAreaActivity.class);
                                                        ProfileActivity.this.startActivity(appIntent);
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // User cancelled the dialog
                                                        Drawable d = getResources().getDrawable(R.drawable.tickclear);
                                                        finishButton.setBackground(d);
                                                    }
                                                })
                                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialog) {
                                                        // User cancelled the dialog
                                                        Drawable d = getResources().getDrawable(R.drawable.tickclear);
                                                        finishButton.setBackground(d);
                                                    }
                                                });
                                        final AlertDialog dialog = builder.create();
                                        dialog.show();

                                        //updatePass(currentPassword);
                                    }
                                    else{
                                    Log.d(TAG,"went horribly wrong somehow.....");
                                }
                                }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
            }
        });

        addStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = getResources().getDrawable(R.drawable.plusfull);
                addStoreButton.setBackground(d);
                Intent appIntent = new Intent(ProfileActivity.this, ShopRegister1Activity.class);
                ProfileActivity.this.startActivity(appIntent);
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailProfile.getText().toString())) {
            emailProfile.setError("Required");
            result = false;
        } else {
            emailProfile.setError(null);
        }
        if (TextUtils.isEmpty(countryProfile.getText().toString())) {
            countryProfile.setError("Required");
            result = false;
        } else {
            countryProfile.setError(null);
        }
        if (TextUtils.isEmpty(cityProfile.getText().toString())) {
            cityProfile.setError("Required");
            result = false;
        } else {
            cityProfile.setError(null);
        }
        return result;
    }

    public String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}

