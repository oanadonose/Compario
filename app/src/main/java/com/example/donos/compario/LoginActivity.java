package com.example.donos.compario;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final DBManager dbManager = new DBManager(this);

        final EditText emaill = (EditText) findViewById(R.id.emailL);
        final EditText passwordd = (EditText) findViewById(R.id.passwordL);
        final Button login = (Button) findViewById(R.id.loginBtn);
        final TextView registerLink = (TextView) findViewById(R.id.registerHere);


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
                final String email = emaill.getText().toString();
                final String password = passwordd.getText().toString();
                String[] emailProjection = {"Email"};
                String emailSelection = "Email=?";
                String[] eselectionArgs = {email};
                Cursor emailCur = dbManager.query(emailProjection,emailSelection,eselectionArgs,DBManager.ColEmail);
                if(emailCur.getCount()>0) {
                    String [] passwordProjection = {"Email","Password"};
                    String passwordSelection = "Email=? AND Password=?";
                    String[] pselectionArgs = {email,password};
                    Cursor matchCur = dbManager.query(passwordProjection,passwordSelection,pselectionArgs,DBManager.ColEmail);
                    if(matchCur.getCount()>0){
                        Toast.makeText(getApplicationContext(),"Login succesful.",Toast.LENGTH_LONG).show();
                        //Change activity
                        /*Intent appIntent = new Intent(LoginActivity.this, UserAreaActivity.class);
                        LoginActivity.this.startActivity(appIntent);*/
                    }
                    else if(matchCur.getCount()==0){
                        Toast.makeText(getApplicationContext(),"Please try again. Password does not match.",Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"no email exists like that",Toast.LENGTH_LONG).show();

            }

            ;
        });
//       login.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               String email = emaill.getText().toString();
//               String password = passwordd.getText().toString();
//               if(email.equals("")||password.equals(""))
//                   Toast.makeText(LoginActivity.this,"All fields are mandatory.",Toast.LENGTH_LONG).show();
//               if(db.checkEmail(email)){
//                   Toast.makeText(LoginActivity.this,"There is no account associated with this email.",Toast.LENGTH_SHORT).show();
//               }
//               else
//               {
//
//               }
//
//           }
//       });


        }
            /* CHECK EMAIL
                String[] projection = {"Email"};
                String selection = "Email=?";
                String[] selectionArgs = {email};
                Cursor cursor = dbManager.query(projection,selection,selectionArgs,DBManager.ColEmail);
                if(cursor.getCount()>0)
                    Toast.makeText(getApplicationContext(),"Email already exists.",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"no email exists like that",Toast.LENGTH_LONG).show();
            */
    }

