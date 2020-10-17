package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.AttractionsListActivityController;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AttractionsListActivity extends AppCompatActivity implements Constants {

    private AttractionsListActivityController attractionsListActivityController;
    private RecyclerView recyclerViewAttractionsList;
    private ProgressBar progressBarAttractionLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions_list);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViewComponents();
        initializeController();
        findByRsql();
        addRecyclerViewOnScrollListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attractions_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    private void onOptionsItemSelectedHelper(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.button_see_attractions_on_map:
                startMapsActivity();
                break;
            case R.id.search_button_menu_attractions_list:
                showBottomSheetFilters();
                break;
        }
    }

    private void initializeViewComponents() {
        recyclerViewAttractionsList = findViewById(R.id.recycler_view_attractions_list);
        progressBarAttractionLoadMore = findViewById(R.id.progress_bar_attraction_load_more);
    }

    private void initializeController() {
        attractionsListActivityController = new AttractionsListActivityController(this);
    }

    private void findByRsql(){
        attractionsListActivityController.findByRsql(getPointSearch(), "0");
    }

    private void addRecyclerViewOnScrollListener(){
        attractionsListActivityController.addRecyclerViewOnScrollListener();
    }

    private void startMapsActivity() {
        attractionsListActivityController.startMapsActivity();
    }

    private void showBottomSheetFilters(){
        attractionsListActivityController.showBottomSheetFilters();
    }

    private PointSearch getPointSearch() {
        return attractionsListActivityController.getPointSearch();
    }

    public RecyclerView getRecyclerViewAttractionsList() {
        return recyclerViewAttractionsList;
    }

    public ProgressBar getProgressBarAttractionLoadMore() {
        return progressBarAttractionLoadMore;
    }
}