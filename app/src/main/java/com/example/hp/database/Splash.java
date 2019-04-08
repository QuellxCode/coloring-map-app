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

                //till removed authentication part
                Intent Intent = new Intent(Splash.this, Login_page.class);
                startActivity(Intent);
                }
            //max time variable
        },max_time);
    }
    }

