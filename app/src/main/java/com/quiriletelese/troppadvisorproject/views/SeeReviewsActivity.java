package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SeeReviewsActivityController;
import com.todkars.shimmer.ShimmerRecyclerView;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class SeeReviewsActivity extends AppCompatActivity {

    private SeeReviewsActivityController seeReviewsActivityController;
    private ShimmerRecyclerView shimmerRecyclerViewSeeReviews;
    private ProgressBar progressBarLoadMore;

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
        initializeRecyclerViewsFakeContent();
        initializeRecyclerView();
        addRecyclerViewOnScrollListener();
    }

    private void initializeViesComponents() {
        shimmerRecyclerViewSeeReviews = findViewById(R.id.recycler_view_see_reviews);
        progressBarLoadMore = findViewById(R.id.progress_bar_reviews_load_more);
    }

    private void setToolbarSubtitle(){
        seeReviewsActivityController.setToolbarSubtitle();
    }

    private void initializeController() {
        seeReviewsActivityController = new SeeReviewsActivityController(this);
    }

    private void initializeRecyclerViewsFakeContent(){
        seeReviewsActivityController.initializeRecyclerViewsFakeContent();
    }

    private void initializeRecyclerView() {
        seeReviewsActivityController.intializeRecyclerView();
    }

    private void addRecyclerViewOnScrollListener(){
        seeReviewsActivityController.addRecyclerViewOnScrollListener();
    }

    public ShimmerRecyclerView getShimmerRecyclerViewSeeReviews() {
        return shimmerRecyclerViewSeeReviews;
    }

    public ProgressBar getProgressBarLoadMore() {
        return progressBarLoadMore;
    }
}