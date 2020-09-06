package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantsListActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Point;

import java.util.List;

public class RestaurantsListActivity extends AppCompatActivity implements Constants {

    private RestaurantsListActivityController restaurantsListActivityController;
    private RecyclerView recyclerViewRestaurantsList;
    private ProgressBar progressBarRestaurantLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        initializeViewComponents();
        initializeController();
        restaurantsListActivityController.initializeRecyclerView((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));
        restaurantsListActivityController.addRecyclerViewOnScrollListener((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));

    }

    private void initializeViewComponents() {
        recyclerViewRestaurantsList = findViewById(R.id.recycler_view_restaurants_list);
        progressBarRestaurantLoadMore = findViewById(R.id.progress_bar_restaurant_load_more);
    }

    private void initializeController() {
        restaurantsListActivityController = new RestaurantsListActivityController(this);
    }

    public RecyclerView getRecyclerViewRestaurantsList() {
        return recyclerViewRestaurantsList;
    }

    public ProgressBar getProgressBarRestaurantLoadMore() {
        return progressBarRestaurantLoadMore;
    }
}