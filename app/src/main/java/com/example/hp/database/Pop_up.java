package com.example.hp.database;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class Pop_up extends Activity {


    Button red,green,blue,yellow,pink;
    public static String colour;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //set pop_up's width and height
        getWindow().setLayout((int)(width*.9),(int)(height*.1));

        //colour buttons

        red = findViewById(R.id.red);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FF0F0F");
                finish();
            }
        });

        green = findViewById(R.id.green);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#1CFF1C");
                finish();
            }
        });

        blue = findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#2F0FFF");
                finish();
            }
        });

        pink = findViewById(R.id.pink);
        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FF1FE9");
                finish();
            }
        });

        yellow = findViewById(R.id.yellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FFFF19");
                finish();
            }
        });

    }
    //get colour from click event
    public void put_colour(String color) {
        colour = color;
    }
    //pass it to colour layer function (Login_page)
    public static String get_colour() {

        return colour;
    }
}