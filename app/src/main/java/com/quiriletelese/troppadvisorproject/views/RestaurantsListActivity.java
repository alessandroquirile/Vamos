package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantsListActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class RestaurantsListActivity extends AppCompatActivity implements Constants {

    private RestaurantsListActivityController restaurantsListActivityController;
    private RecyclerView recyclerViewRestaurantsList;
    private ProgressBar progressBarRestaurantLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViewComponents();
        initializeController();
        findByRsql();
        addRecyclerViewOnScrollListener();

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurants_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return onOptionItemSelectedHelper(item);
    }

    private boolean onOptionItemSelectedHelper(@NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.button_see_restaurants_on_map:
                startRestaurantMapActivity();
                break;
            case R.id.button_filter_restaurants_list:
                showBottomSheetFilters();
                break;
        }
        return true;
    }

    private void initializeViewComponents() {
        recyclerViewRestaurantsList = findViewById(R.id.recycler_view_restaurants_list);
        progressBarRestaurantLoadMore = findViewById(R.id.progress_bar_restaurant_load_more);
    }

    private void initializeController() {
        restaurantsListActivityController = new RestaurantsListActivityController(this);
    }

    private void findByRsql(){
        restaurantsListActivityController.findByRsql(null, getPointSearch(), "0");
    }

    private void addRecyclerViewOnScrollListener(){
        restaurantsListActivityController.addRecyclerViewOnScrollListener();
    }

    private void startRestaurantMapActivity() {
        restaurantsListActivityController.startRestaurantMapActivity();
    }

    private void showBottomSheetFilters() {
        restaurantsListActivityController.showBottomSheetFilters();
    }

    private PointSearch getPointSearch() {
        return restaurantsListActivityController.getPointSearch();
    }

    public RecyclerView getRecyclerViewRestaurantsList() {
        return recyclerViewRestaurantsList;
    }

    public ProgressBar getProgressBarRestaurantLoadMore() {
        return progressBarRestaurantLoadMore;
    }
}