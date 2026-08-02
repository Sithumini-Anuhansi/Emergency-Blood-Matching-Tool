package com.bloodnetwork.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Entry point — immediately redirects to SplashActivity which handles loading.
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
