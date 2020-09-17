package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SeeReviewsActivityController;

public class SeeReviewsActivity extends AppCompatActivity {

    private SeeReviewsActivityController seeReviewsActivityController;
    private RecyclerView recyclerViewSeeReviews;
    private ProgressBar progressBarReviewsLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reviews);

        Toolbar toolbar = findViewById(R.id.tool_bar_see_reviews);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViesComponents();
        initializeController();
        setToolbarSubtitle();
        initializeRecyclerView();

    }

    private void initializeViesComponents() {
        recyclerViewSeeReviews = findViewById(R.id.recycler_view_see_reviews);
        progressBarReviewsLoadMore = findViewById(R.id.progress_bar_reviews_load_more);
    }

    private void initializeController() {
        seeReviewsActivityController = new SeeReviewsActivityController(this);
    }

    private void setToolbarSubtitle(){
        seeReviewsActivityController.setToolbarSubtitle();
    }

    private void initializeRecyclerView() {
        seeReviewsActivityController.intializeRecyclerView();
    }

    public RecyclerView getRecyclerViewSeeReviews() {
        return recyclerViewSeeReviews;
    }

    public ProgressBar getProgressBarReviewsLoadMore() {
        return progressBarReviewsLoadMore;
    }
}