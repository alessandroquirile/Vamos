package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.SeeReviewsActivityController;
import com.quiriletelese.troppadvisorproject.controllers.UserReviewsActivityController;
import com.todkars.shimmer.ShimmerRecyclerView;

import java.util.Objects;

public class UserReviewsActivity extends AppCompatActivity {

    private UserReviewsActivityController userReviewsActivityController;
    private ShimmerRecyclerView shimmerRecyclerViewSeeReviews;
    private ProgressBar progressBarLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

        Toolbar toolbar = findViewById(R.id.tool_bar_see_user_reviews);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViewsComponents();
        initializeController();
        initializeRecyclerViewsFakeContent();
        initializeRecyclerView();
        addRecyclerViewOnScrollListener();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    private void onOptionsItemSelectedHelper(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
    }

    private void initializeViewsComponents() {
        shimmerRecyclerViewSeeReviews = findViewById(R.id.recycler_view_see_user_reviews);
        progressBarLoadMore = findViewById(R.id.progress_bar_user_reviews_load_more);
    }

    private void initializeController() {
        userReviewsActivityController = new UserReviewsActivityController(this);
    }

    private void initializeRecyclerViewsFakeContent() {
        userReviewsActivityController.initializeRecyclerViewsFakeContent();
    }

    private void initializeRecyclerView() {
        userReviewsActivityController.intializeRecyclerView();
    }

    private void addRecyclerViewOnScrollListener() {
        userReviewsActivityController.addRecyclerViewOnScrollListener();
    }

    public ShimmerRecyclerView getShimmerRecyclerViewSeeReviews() {
        return shimmerRecyclerViewSeeReviews;
    }

    public ProgressBar getProgressBarLoadMore() {
        return progressBarLoadMore;
    }
}