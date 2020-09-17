package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewRestaurantsListAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.RestaurantMapActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.List;

public class RestaurantsListActivityController implements Constants {

    private RestaurantsListActivity restaurantsListActivity;
    private DAOFactory daoFactory;
    private RecyclerViewRestaurantsListAdapter recyclerViewRestaurantsListAdapter;
    private int page = 0, size = 3;
    private boolean loadData = false;

    public RestaurantsListActivityController(RestaurantsListActivity restaurantsListActivity) {
        this.restaurantsListActivity = restaurantsListActivity;
    }

    public void findRestaurantsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, int page, int size) {
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty(RESTAURANT_STORAGE_TECHNOLOGY, restaurantsListActivity.getApplicationContext()));
        restaurantDAO.findByPointNear(volleyCallBack, pointSearch, restaurantsListActivity.getApplicationContext(), page, size);
    }

    public void initializeRecyclerView(PointSearch pointSearch) {
        findRestaurantsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {

            }
        }, pointSearch, this.page, this.size);
    }

    public void addRecyclerViewOnScrollListener(final PointSearch pointSearch) {
        restaurantsListActivity.getRecyclerViewRestaurantsList().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScrollingDown(dy))
                    if (isScrolledToLastItem(recyclerView))
                        loadMoreRestaurants(pointSearch);
            }
        });
    }

    private boolean isScrollingDown(int dy) {
        return dy > 0;
    }

    private boolean isScrolledToLastItem(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    private void loadMoreRestaurants(PointSearch pointSearch) {
        loadData = true;
        if (loadData) {
            loadData = false;
            setProgressBarLoadMoreVisible();
            findRestaurantsByPointNear(new VolleyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    addNewRestaurantsToList((List<Restaurant>) object);

                }

                @Override
                public void onError(String errorCode) {
                    restaurantsListActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastNoMoreRestaurants();
                        }
                    });
                }
            }, pointSearch, page += 1, size);
        }
    }

    private void initializeRecyclerViewOnSuccess(List<Restaurant> restaurants) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(restaurantsListActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRestaurantsListAdapter = new RecyclerViewRestaurantsListAdapter(restaurantsListActivity.getApplicationContext(), restaurants);
        restaurantsListActivity.getRecyclerViewRestaurantsList().setLayoutManager(linearLayoutManager);
        restaurantsListActivity.getRecyclerViewRestaurantsList().setAdapter(recyclerViewRestaurantsListAdapter);
    }

    private void addNewRestaurantsToList(List<Restaurant> restaurants) {
        recyclerViewRestaurantsListAdapter.addListItems(restaurants);
        recyclerViewRestaurantsListAdapter.notifyDataSetChanged();
        setProgressBarLoadMoreInvisible();
    }

    private void showToastNoMoreRestaurants() {
        setProgressBarLoadMoreInvisible();
        Toast.makeText(restaurantsListActivity, restaurantsListActivity.getResources().getString(R.string.end_of_results), Toast.LENGTH_SHORT).show();
    }

    private void setProgressBarLoadMoreVisible() {
        ProgressBar progressBar = restaurantsListActivity.getProgressBarRestaurantLoadMore();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setProgressBarLoadMoreInvisible() {
        ProgressBar progressBar = restaurantsListActivity.getProgressBarRestaurantLoadMore();
        progressBar.setVisibility(View.GONE);
    }

    public void startRestaurantMapActivity() {
        Intent restaurantMapActivity = new Intent(restaurantsListActivity.getApplicationContext(), RestaurantMapActivity.class);
        restaurantMapActivity.putExtra(POINT_SEARCH, restaurantsListActivity.getIntent().getSerializableExtra(POINT_SEARCH));
        restaurantsListActivity.startActivity(restaurantMapActivity);
    }

}
