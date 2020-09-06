package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HotelsListActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;

public class HotelsListActivity extends AppCompatActivity implements Constants {

    private HotelsListActivityController hotelsListActivityController;
    private RecyclerView recyclerViewHotelsList;
    private ProgressBar progressBarHotelLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_list);

        initializeViewComponents();
        initializeController();
        hotelsListActivityController.initializeRecyclerView((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));
        hotelsListActivityController.addRecyclerViewOnScrollListener((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));

    }

    private void initializeViewComponents() {
        recyclerViewHotelsList = findViewById(R.id.recycler_view_hotels_list);
        progressBarHotelLoadMore = findViewById(R.id.progress_bar_hotel_load_more);
    }

    private void initializeController() {
        hotelsListActivityController = new HotelsListActivityController(this);
    }

    public RecyclerView getRecyclerViewHotelsList() {
        return recyclerViewHotelsList;
    }

    public ProgressBar getProgressBarHotelLoadMore() {
        return progressBarHotelLoadMore;
    }
}