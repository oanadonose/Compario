package com.example.donos.compario;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        final DBManager dbManager = new DBManager(this);

        final EditText emailr = (EditText) findViewById(R.id.emailR);
        final EditText usernamer = (EditText) findViewById(R.id.usernameR);
        final EditText passwordr = (EditText) findViewById(R.id.passwordR);
        final Button register = (Button) findViewById(R.id.registerBtn);
        final Button typeBtn = (Button) findViewById(R.id.typeBtn);
        String typeOfUser = "";
        final String[] typeArray = {"Company", "Person"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked OK button
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User cancelled the dialog
//            }
//        });
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
                EditText etEmail = (EditText) findViewById(R.id.emailR);
                EditText etName = (EditText) findViewById(R.id.usernameR);
                EditText etPassword = (EditText) findViewById(R.id.passwordR);
                String email = etEmail.getText().toString();
                String type = isChecked;
//                if(checkEmail(email))
//                {
//                    Toast.makeText(getApplicationContext(),"TRUE",Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(),"FALSE",Toast.LENGTH_LONG).show();
//                }
                String[] projection = {"Email"};
                String selection = "Email=?";
                String[] selectionArgs = {email};
                Cursor cursor = dbManager.query(projection, selection, selectionArgs, DBManager.ColEmail);
                if (cursor.getCount() > 0) {
                    Toast.makeText(getApplicationContext(), "Email already exists.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "no email exists like that", Toast.LENGTH_LONG).show();
                    ContentValues values = new ContentValues();
                    values.put(DBManager.ColEmail, email);
                    values.put(DBManager.ColUserName, etName.getText().toString());
                    values.put(DBManager.ColPassword, etPassword.getText().toString());
                    values.put(DBManager.ColType, type);
                    long id = dbManager.Insert(values);
                    if (id > 0)
                        Toast.makeText(getApplicationContext(), "user id: " + id, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "cannot insert", Toast.LENGTH_LONG).show();
                }
            }
    });

//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = usernamer.getText().toString();
//                String email = emailr.getText().toString();
//                String password = passwordr.getText().toString();
//                String type = isChecked;
//                //Toast.makeText(RegisterActivity.this, "Registered as "+name+" "+email+" "+ isChecked, Toast.LENGTH_SHORT).show();
//                if (name.equals("") || email.equals("") || password.equals("") || type.equals(""))
//                    Toast.makeText(RegisterActivity.this, "All fields are mandatory.", Toast.LENGTH_LONG).show();
//                else {
//                    if (db.checkEmail(email)) {
//                        db.insertData(email, name, password, type);
//                        Toast.makeText(RegisterActivity.this, "Register succesful.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(RegisterActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

}

    //    public void buSave(View view){
//        EditText etEmail = (EditText) findViewById(R.id.emailR);
//        EditText etName = (EditText) findViewById(R.id.usernameR);
//        EditText etPassword = (EditText) findViewById(R.id.passwordR);
//        String type = isChecked;
//        ContentValues values = new ContentValues();
//        values.put(DBManager.ColEmail,etEmail.getText().toString());
//        values.put(DBManager.ColUserName,etName.getText().toString());
//        values.put(DBManager.ColPassword,etPassword.getText().toString());
//        values.put(DBManager.ColType,type);
//        long id = dbManager.Insert(values);
//        if(id>0)
//            Toast.makeText(getApplicationContext(),"user id: "+id,Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getApplicationContext(),"cannot insert",Toast.LENGTH_LONG).show();
//    }
//    public void buLoad(View view) {
//        String[] projection = {"Email", "Name", "Password", "Type"};
//        //use null instead of projection for all data
//        Cursor cursor = dbManager.query(null, null, null, DBManager.ColUserName);
//        if (cursor.moveToFirst()) {
//            String tableData = "";
//            do {
//                tableData += cursor.getString(cursor.getColumnIndex(DBManager.ColUserName)) + " , " +
//                        cursor.getString(cursor.getColumnIndex(DBManager.ColPassword)) + " , " +
//                        cursor.getString(cursor.getColumnIndex(DBManager.ColEmail)) + "::";
//            } while (cursor.moveToNext());
//            Toast.makeText(getApplicationContext(), tableData, Toast.LENGTH_LONG).show();
//        }
//    }
//    public boolean checkEmail(View view){
//        String[] projection = {"Email"};
//        Cursor cursor = dbManager.query(projection,DBManager.ColEmail,null,DBManager.ColEmail);
//        if(cursor.getCount()>0)
//            Toast.makeText(getApplicationContext(),"Email already exists.",Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getApplicationContext(),"no email exists like that",Toast.LENGTH_LONG).show();
//
//    }
//    public boolean checkEmail(String email) {
//        String[] projection = {"Email"};
//        String selection = "Email=?";
//        String[] selectionArgs = {email};
//        Cursor cursor = dbManager.query(projection, selection, selectionArgs, DBManager.ColEmail);
//        if (cursor.getCount() > 0) {
//            Toast.makeText(getApplicationContext(), "Email already exists.", Toast.LENGTH_LONG).show();
//            return false;
//        } else
//            return true;
//    }
} //closes appcompat



