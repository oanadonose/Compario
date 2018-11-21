package com.example.donos.compario;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    public String isChecked;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        final EditText emailRegister = (EditText) findViewById(R.id.emailRegister);
        final EditText usernameRegister = (EditText) findViewById(R.id.usernameRegister);
        final EditText passwordRegister = (EditText) findViewById(R.id.passwordRegister);
        final Button register = (Button) findViewById(R.id.registerBtn);
        final Button typeBtn = (Button) findViewById(R.id.typeBtn);
        Typeface typeface = getResources().getFont(R.font.worksans);
        emailRegister.setTypeface(typeface);
        usernameRegister.setTypeface(typeface);
        passwordRegister.setTypeface(typeface);
        register.setTypeface(typeface);
        typeBtn.setTypeface(typeface);
        String typeOfUser = "";
        final String[] typeArray = {"Company", "Person"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(typeOfUser);
        builder.setItems(typeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isChecked = typeArray[which];
                Toast.makeText(RegisterActivity.this, isChecked, Toast.LENGTH_SHORT).show();
            }
        });
//Changing button to show the choice
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isChecked == "Person") {
                    typeBtn.setText("Person");
                } else {
                    typeBtn.setText("Company");
                }
            }
        });
// Create the AlertDialog
        final AlertDialog dialog = builder.create();
        typeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = usernameRegister.getText().toString();
                String email = emailRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                String type = isChecked;
                if (name.equals("") || email.equals("") || password.equals("") || type.equals(""))
                    Toast.makeText(RegisterActivity.this, "All fields are mandatory.", Toast.LENGTH_LONG).show();
                else{
                    //create query: SELECT * FROM USERS WHERE EMAIL = ? (EMAILREGISTER)
                    String[] projection = {"Email"};
                    String selection = "Email=?";
                    String[] selectionArgs = {email};
                    Cursor cursor = dbManager.query(projection, selection, selectionArgs, DBManager.ColEmail);

                    if (cursor.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "Email already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(DBManager.ColEmail, email);
                        values.put(DBManager.ColUserName, name);
                        values.put(DBManager.ColPassword, password);
                        values.put(DBManager.ColType, type);
                        long id = dbManager.Insert(values); //INSERT VALUES INTO DATABASE
                        if (id > 0)
                            Toast.makeText(getApplicationContext(), "user id: " + id, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "cannot insert", Toast.LENGTH_LONG).show();
                    }
                }
            }
    });
}
}
