package com.srteam.expensetracker.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.srteam.expensetracker.R;
import com.srteam.expensetracker.SignupActivity;
import com.srteam.expensetracker.signinActivity;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);


        Thread myThread = new Thread(new Runnable() {
            public void run() {
                if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                    try {
                        sleep(3000);
                        Intent splashIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(splashIntent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        sleep(3000);
                        Intent splashIntent = new Intent(getApplicationContext(), signinActivity.class);
                        startActivity(splashIntent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });



        myThread.start();


    }
}
