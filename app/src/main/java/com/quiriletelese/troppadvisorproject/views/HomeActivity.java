package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HomePageFragmentController;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    private HomePageFragmentController homePageFragmentController;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayoutHomeFrament;
    private RecyclerView recyclerViewAttractions;
    private ProgressBar progressBarAttractionHomeLoadMore;
    private View viewNoGeolocationError, viewNoAttractionsError, viewMissingLocationPermissionError;
    private Button buttonProvidePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.tool_bar_home);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        initializeViewComponents();
        initializeHomePageFragmentController();
        setListenerOnViewComponents();
        addRecyclerViewOnScrollListener();
        checkDailyReward();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        homePageFragmentController.onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_attractions:
                startSearchAttractionsActivity();
                break;
            case R.id.profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
        }
    }

    private void initializeViewComponents() {
        swipeRefreshLayoutHomeFrament = findViewById(R.id.swipe_refresh_layout_home_fragment);
        swipeRefreshLayoutHomeFrament.setRefreshing(true);
        recyclerViewAttractions = findViewById(R.id.recycler_view_attraction);
        progressBarAttractionHomeLoadMore = findViewById(R.id.progress_bar_attraction_home_load_more);
        viewNoGeolocationError = findViewById(R.id.no_geolocation_activated_error_layout);
        viewNoAttractionsError = findViewById(R.id.no_attractions_error);
        viewMissingLocationPermissionError = findViewById(R.id.missing_location_permission_error_layout);
        buttonProvidePermission = findViewById(R.id.button_provide_permission);
    }

    private void initializeHomePageFragmentController() {
        homePageFragmentController = new HomePageFragmentController(this);
    }

    private void setListenerOnViewComponents() {
        homePageFragmentController.setListenerOnViewComponents();
    }

    private void addRecyclerViewOnScrollListener() {
        homePageFragmentController.addRecyclerViewOnScrollListener();
    }

    public void checkDailyReward() {
        homePageFragmentController.checkDailyReward();
    }

    private void startSearchAttractionsActivity() {
        homePageFragmentController.startAttractionsListActivity();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SwipeRefreshLayout getSwipeRefreshLayoutHomeFrament() {
        return swipeRefreshLayoutHomeFrament;
    }

    public RecyclerView getRecyclerViewAttractions() {
        return recyclerViewAttractions;
    }

    public ProgressBar getProgressBarAttractionHomeLoadMore() {
        return progressBarAttractionHomeLoadMore;
    }

    public View getViewNoAttractionsError() {
        return viewNoAttractionsError;
    }

    public Button getButtonProvidePermission() {
        return buttonProvidePermission;
    }

    public View getViewNoGeolocationError() {
        return viewNoGeolocationError;
    }

    public View getViewMissingLocationPermissionError() {
        return viewMissingLocationPermissionError;
    }
}