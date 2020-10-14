package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quiriletelese.troppadvisorproject.controllers.SplashScreenActivityController;

public class SplashScreenActivity extends AppCompatActivity {

    private SplashScreenActivityController splashScreenActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeController();
        startNextActivity();

    }

    private void initializeController() {
        splashScreenActivityController = new SplashScreenActivityController(this);
    }

    private void startNextActivity() {
        splashScreenActivityController.startNextActivity();
        finish();
    }

}