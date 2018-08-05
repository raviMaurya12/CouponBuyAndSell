package com.example.lenovo.buyandsell;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_welcome_screen );



        new Handler().postDelayed( new Runnable(){

            @Override
            public void run(){
                Intent i = new Intent(WelcomeScreen.this,MainActivity.class);
                startActivity(i);
                finish();
            }

        },SPLASH_TIME_OUT);

    }
}
