package com.example.hp.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;


public class Splash extends AppCompatActivity {
    int max_time = 500 ; // 2000mili seconds = 2 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //code for ssplashscreen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //checking either id is saved in device storage of not
                SharedPreferences sharedPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
                SharedPreferences sharedPreference = getSharedPreferences("google_Signin_id", Context.MODE_PRIVATE);
                //if one of folder contain the id
                if(!sharedPreferences.getString("id", "").equals("")) {
                    //SplashActivity and login are names of classes(windows)
                    Intent Intent = new Intent(Splash.this, Login_page.class);
                    Intent.putExtra("loginMode", "Manual");
                    startActivity(Intent);
                    //activity terminates
                    finish();
                }
                else if (!sharedPreference.getString("google_id", "").equals("")) {
                    //SplashActivity and login are names of classes(windows)
                    Intent Intent = new Intent(Splash.this, Login_page.class);
                    startActivity(Intent);
                    //activity terminates
                    finish();
                }
                else {
                    Intent homeIntent = new Intent(Splash.this, Login2.class);
                    startActivity(homeIntent);
                    //activity terminates
                    finish();
                }
                }
            //max time variable
        },max_time);
        //copy image and paste in drawable (leftside)
        //click ok > provide the dersied name of your picture
        //go to xml view > text (to link picture with)
    }
    }

