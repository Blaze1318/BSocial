package com.example.firebaseauthenticationandstoragetest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);


        final Intent i = new Intent (this,LoginActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
