package com.example.donos.compario;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import models.User;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mSignUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mSignInButton = findViewById(R.id.buttonSignIn);
        mSignUpButton = findViewById(R.id.buttonSignUp);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);

        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());

                        } else {
                            Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to User Area Activity
        startActivity(new Intent(LoginActivity.this, UserAreaActivity.class));
        finish();
    }
    public String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }
    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            signIn();
        } else if (i == R.id.buttonSignUp) {
            signUp();
        }
    }
    // [END basic_write]
}

//    DBManager dbManager;
//    Context context;
//    public static String accountEmail;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        //code to make status bar and navigation bar transparent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            }
//        }
//        final DBManager dbManager = new DBManager(this);
//        final SharedPreferences mSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
//        final SharedPreferences.Editor editor = mSettings.edit();
//        final EditText emailLogin = (EditText) findViewById(R.id.emailLogin);
//        final EditText passwordLogin = (EditText) findViewById(R.id.passwordL);
//        final Button login = (Button) findViewById(R.id.loginBtn);
//        final TextView registerLink = (TextView) findViewById(R.id.registerHere);
//        final CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
//        Typeface typeface = getResources().getFont(R.font.worksans);
//        registerLink.setTypeface(typeface);
//        emailLogin.setTypeface(typeface);
//        passwordLogin.setTypeface(typeface);
//        checkBoxRememberMe.setTypeface(typeface);
//        login.setTypeface(typeface);
//
//        /*FIRST TEST FIREBASE
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");
//        Toast.makeText(this,"check data",Toast.LENGTH_LONG).show();
//        */
//
//        /*Checking if there is a user saved*/
//        if (mSettings.contains("pref_email")) {
//            String emailPref = mSettings.getString("pref_email", "not found");
//            emailLogin.setText(emailPref);
//        }
//        if (mSettings.contains("pref_pass")) {
//            String passPref = mSettings.getString("pref_pass", "not found");
//            passwordLogin.setText(passPref);
//        }
//        if (mSettings.contains("pref_check")) {
//            Boolean checkPref = mSettings.getBoolean("pref_check", false);
//            checkBoxRememberMe.setChecked(checkPref);
//        }
//
////Setting the link to direct you to register page
//        registerLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
//                LoginActivity.this.startActivity(registerIntent);
//            }
//        });
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String email = emailLogin.getText().toString();
//                final String password = passwordLogin.getText().toString();
//                String[] emailProjection = {"Email"};
//                String emailSelection = "Email=?";
//                String[] eselectionArgs = {email};
//                Cursor emailCur = dbManager.query(emailProjection, emailSelection, eselectionArgs, DBManager.ColEmail);
//                if (emailCur.getCount() > 0) {
//                    String[] passwordProjection = {"Email", "Password"};
//                    String passwordSelection = "Email=? AND Password=?";
//                    String[] pselectionArgs = {email, password};
//                    Cursor matchCur = dbManager.query(passwordProjection, passwordSelection, pselectionArgs, DBManager.ColEmail);
//                    if (matchCur.getCount() > 0) {
//                        Toast.makeText(getApplicationContext(), "Login succesful.", Toast.LENGTH_LONG).show();
//                        //save details if box is checked
//                        if (checkBoxRememberMe.isChecked()) {
//                            editor.putString("pref_email", email);
//                            editor.putString("pref_pass", password);
//                            editor.putBoolean("pref_check", checkBoxRememberMe.isChecked());
//                            editor.apply();
//                        }
//
//                        accountEmail = email;
//                        //Change activity
//                        Intent appIntent = new Intent(LoginActivity.this,UserAreaActivity.class);
//                        LoginActivity.this.startActivity(appIntent);
//                    } else if (matchCur.getCount() == 0) {
//                        Toast toast = Toast.makeText(getApplicationContext(), "Please try again. Password does not match.", Toast.LENGTH_LONG);
//                        toast.show();
//                    }
//                } else {
//                    Toast toast = Toast.makeText(getApplicationContext(), "no email exists like that", Toast.LENGTH_LONG);
//                    toast.show();
//                }
//            }
//        });
//    }
//}

