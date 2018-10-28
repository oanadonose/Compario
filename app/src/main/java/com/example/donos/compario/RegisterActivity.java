package com.example.donos.compario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText username = (EditText) findViewById(R.id.usernameR);
        final EditText password = (EditText) findViewById(R.id.passwordR);
        final Button register = (Button) findViewById(R.id.registerBtn);
    }
}
