package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SeeReviewsActivityController;
import com.todkars.shimmer.ShimmerRecyclerView;

import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewsComponents();
        initializeController();
        setToolbarSubtitle();
        initializeRecyclerViewsFakeContent();
        initializeRecyclerView();
        addRecyclerViewOnScrollListener();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void initializeViewsComponents() {
        shimmerRecyclerViewSeeReviews = findViewById(R.id.recycler_view_see_reviews);
        progressBarLoadMore = findViewById(R.id.progress_bar_reviews_load_more);
    }

    private void setToolbarSubtitle() {
        seeReviewsActivityController.setToolbarSubtitle();
    }

    private void initializeController() {
        seeReviewsActivityController = new SeeReviewsActivityController(this);
    }

    private void initializeRecyclerViewsFakeContent() {
        seeReviewsActivityController.initializeRecyclerViewsFakeContent();
    }

    private void initializeRecyclerView() {
        seeReviewsActivityController.intializeRecyclerView();
    }

    private void addRecyclerViewOnScrollListener() {
        seeReviewsActivityController.addRecyclerViewOnScrollListener();
    }

    public ShimmerRecyclerView getShimmerRecyclerViewSeeReviews() {
        return shimmerRecyclerViewSeeReviews;
    }

    public ProgressBar getProgressBarLoadMore() {
        return progressBarLoadMore;
    }
}