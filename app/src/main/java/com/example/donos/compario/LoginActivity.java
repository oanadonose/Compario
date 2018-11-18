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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    DBManager dbManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //code to make status bar and navigation bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        final DBManager dbManager = new DBManager(this);
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = mSettings.edit();
        final EditText emailLogin = (EditText) findViewById(R.id.emailLogin);
        final EditText passwordLogin = (EditText) findViewById(R.id.passwordL);
        final Button login = (Button) findViewById(R.id.loginBtn);
        final TextView registerLink = (TextView) findViewById(R.id.registerHere);
        final CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        Typeface typeface = getResources().getFont(R.font.worksans);
        registerLink.setTypeface(typeface);
        emailLogin.setTypeface(typeface);
        passwordLogin.setTypeface(typeface);
        checkBoxRememberMe.setTypeface(typeface);
        login.setTypeface(typeface);

        /*Checking if there is a user saved*/
        if (mSettings.contains("pref_email")) {
            String emailPref = mSettings.getString("pref_email", "not found");
            emailLogin.setText(emailPref);
        }
        if (mSettings.contains("pref_pass")) {
            String passPref = mSettings.getString("pref_pass", "not found");
            passwordLogin.setText(passPref);
        }
        if (mSettings.contains("pref_check")) {
            Boolean checkPref = mSettings.getBoolean("pref_check", false);
            checkBoxRememberMe.setChecked(checkPref);
        }

//Setting the link to direct you to register page
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailLogin.getText().toString();
                final String password = passwordLogin.getText().toString();
                String[] emailProjection = {"Email"};
                String emailSelection = "Email=?";
                String[] eselectionArgs = {email};
                Cursor emailCur = dbManager.query(emailProjection, emailSelection, eselectionArgs, DBManager.ColEmail);
                if (emailCur.getCount() > 0) {
                    String[] passwordProjection = {"Email", "Password"};
                    String passwordSelection = "Email=? AND Password=?";
                    String[] pselectionArgs = {email, password};
                    Cursor matchCur = dbManager.query(passwordProjection, passwordSelection, pselectionArgs, DBManager.ColEmail);
                    if (matchCur.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "Login succesful.", Toast.LENGTH_LONG).show();
                        //save details if box is checked
                        if (checkBoxRememberMe.isChecked()) {
                            editor.putString("pref_email", email);
                            editor.putString("pref_pass", password);
                            editor.putBoolean("pref_check", checkBoxRememberMe.isChecked());
                            editor.apply();
                        }

                        //Change activity
                        Intent appIntent = new Intent(LoginActivity.this,UserAreaActivity.class);
                        LoginActivity.this.startActivity(appIntent);
                    } else if (matchCur.getCount() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please try again. Password does not match.", Toast.LENGTH_LONG);
                        // set message color
                        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                        textView.setTextColor(Color.YELLOW);
                        // set background color
                        toast.getView().setBackgroundColor(getResources().getColor(R.color.purple));
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "no email exists like that", Toast.LENGTH_LONG);
                    TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                    textView.setTextColor(Color.YELLOW);
                    toast.show();
                    // set background color
                    toast.getView().setBackgroundColor(getResources().getColor(R.color.purple));
                    toast.show();
                }
            }
        });
    }
}

