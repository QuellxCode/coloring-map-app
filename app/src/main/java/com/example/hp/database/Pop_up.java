package com.example.hp.database;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class Pop_up extends Activity {


    Button red,green,blue,yellow,pink,orange,purple,light_blue,brown;
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

        red = findViewById(R.id.rede);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#B50606");
                setResult(RESULT_OK);
                finish();
            }
        });

        green = findViewById(R.id.green);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#075E04");
                setResult(RESULT_OK);
                finish();
            }
        });

        blue = findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#2F0FFF");
                setResult(RESULT_OK);
                finish();
            }
        });

        pink = findViewById(R.id.pink);
        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FF1FE9");
                setResult(RESULT_OK);
                finish();
            }
        });

        yellow = findViewById(R.id.yellow);
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FFFF19");
                setResult(RESULT_OK);
                finish();
            }
        });

        orange = findViewById(R.id.orange);
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#FFBF0F");
                setResult(RESULT_OK);
                finish();
            }
        });

        purple = findViewById(R.id.purple);
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#641980");
                setResult(RESULT_OK);
                finish();
            }
        });

        light_blue = findViewById(R.id.light_blue);
        light_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#1FFFFF");
                setResult(RESULT_OK);
                finish();
            }
        });

        brown = findViewById(R.id.brown);
        brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_colour("#80411C");
                setResult(RESULT_OK);
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
