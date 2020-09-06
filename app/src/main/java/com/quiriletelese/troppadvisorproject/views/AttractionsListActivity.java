package com.quiriletelese.troppadvisorproject.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionsListAcitivityController;
import com.quiriletelese.troppadvisorproject.controllers.RestaurantsListActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;

public class AttractionsListActivity extends AppCompatActivity implements Constants {

    private AttractionsListAcitivityController attractionsListAcitivityController;
    private RecyclerView recyclerViewAttractionsList;
    private ProgressBar progressBarAttractionLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_list);

        initializeViewComponents();
        initializeController();
        attractionsListAcitivityController.initializeRecyclerView((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));
        attractionsListAcitivityController.addRecyclerViewOnScrollListener((PointSearch) getIntent().getSerializableExtra(POINT_SEARCH));
    }

    private void initializeViewComponents() {
        recyclerViewAttractionsList = findViewById(R.id.recycler_view_attractions_list);
        progressBarAttractionLoadMore = findViewById(R.id.progress_bar_attraction_load_more);
    }

    private void initializeController() {
        attractionsListAcitivityController = new AttractionsListAcitivityController(this);
    }

    public RecyclerView getRecyclerViewAttractionsList() {
        return recyclerViewAttractionsList;
    }

    public ProgressBar getProgressBarAttractionLoadMore() {
        return progressBarAttractionLoadMore;
    }
}