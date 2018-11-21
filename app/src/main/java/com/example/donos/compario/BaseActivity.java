package com.example.donos.compario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private String userID;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

//    public String getUid() {
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        } else {
//            //login or register screen
//            Intent appIntent = new Intent(BaseActivity.this, LoginActivity.class);
//            BaseActivity.this.startActivity(appIntent);
//            throw new Exception();
//        }
//        return uid;
//    }


}