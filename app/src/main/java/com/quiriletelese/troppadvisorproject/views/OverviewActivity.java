package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.WriteReviewController;

public class OverviewActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButtonWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initializeViewComponents();
        initializeController();
    }

    public FloatingActionButton getFloatingActionButtonWriteReview() {
        return floatingActionButtonWriteReview;
    }

    private void initializeViewComponents() {
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_write_review);
    }

    public void initializeController() {
        WriteReviewController writeReviewController = new WriteReviewController(this);
        writeReviewController.setListenersOnOverviewActiviyComponents();
    }
}

