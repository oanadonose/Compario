package com.example.donos.compario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.usernameR);
        final EditText password = (EditText) findViewById(R.id.passwordR);
        final Button login = (Button) findViewById(R.id.loginBtn);
        final TextView registerLink = (TextView) findViewById(R.id.registerHere);

       registerLink.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v)
           {
               Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
               LoginActivity.this.startActivity(registerIntent);
           }
       });
        }
    }
