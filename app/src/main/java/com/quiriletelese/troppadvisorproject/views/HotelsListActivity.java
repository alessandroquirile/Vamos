package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelsListActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelsListActivity extends AppCompatActivity implements Constants {

    private HotelsListActivityController hotelsListActivityController;
    private RecyclerView recyclerViewHotelsList;
    private ProgressBar progressBarHotelLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViewComponents();
        initializeController();
        findByRsql();
        addRecyclerViewOnScrollListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotels_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.button_see_hotels_on_map:
                startMapsActivity();
                break;
            case R.id.search_button_menu_hotels_list:
                showBottomSheetFilters();
                break;
        }
        return true;
    }

    private void initializeViewComponents() {
        recyclerViewHotelsList = findViewById(R.id.recycler_view_hotels_list);
        progressBarHotelLoadMore = findViewById(R.id.progress_bar_hotel_load_more);
    }

    private void initializeController() {
        hotelsListActivityController = new HotelsListActivityController(this);
    }

    private void findByRsql(){
        hotelsListActivityController.findByRsql(getPointSearch(), "0");
    }

    private void addRecyclerViewOnScrollListener(){
        hotelsListActivityController.addRecyclerViewOnScrollListener();
    }

    private void startMapsActivity() {
        hotelsListActivityController.startMapsActivity();
    }

    private void showBottomSheetFilters(){
        hotelsListActivityController.showBottomSheetFilters();
    }

    private PointSearch getPointSearch() {
        return hotelsListActivityController.getPointSearch();
    }

    public RecyclerView getRecyclerViewHotelsList() {
        return recyclerViewHotelsList;
    }

    public ProgressBar getProgressBarHotelLoadMore() {
        return progressBarHotelLoadMore;
    }
}