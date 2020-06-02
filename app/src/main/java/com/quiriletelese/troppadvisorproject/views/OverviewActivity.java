package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;

public class OverviewActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton floatingActionButtonWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        initializeViewComponents();
        setListenerOnViewComponents();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_button_write_review:
                startActivity(new Intent(getApplicationContext(), WriteReviewActivity.class));
                break;
        }
    }

    private void initializeViewComponents() {
        floatingActionButtonWriteReview = findViewById(R.id.floating_action_button_write_review);
    }

    private void setListenerOnViewComponents() {
        floatingActionButtonWriteReview.setOnClickListener(this);
    }
}
