package com.example.donos.compario;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    public String isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText username = (EditText) findViewById(R.id.usernameR);
        final EditText password = (EditText) findViewById(R.id.passwordR);
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

    }
}
