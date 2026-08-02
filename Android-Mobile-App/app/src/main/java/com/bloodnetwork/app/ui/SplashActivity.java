package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.bloodnetwork.app.R;
import com.bloodnetwork.app.system.EmergencyBloodSystem;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Pre-load system in background
        new Thread(() -> {
            try {
                EmergencyBloodSystem.getInstance(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, SPLASH_DELAY);
        }).start();
    }
}
