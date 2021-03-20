package com.quiriletelese.troppadvisorproject.views;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HomePageFragmentController;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HomePageFragment extends Fragment {

    private HomePageFragmentController homePageFragmentController;
    private SwipeRefreshLayout swipeRefreshLayoutHomeFrament;
    private RecyclerView recyclerViewAttractions;
    private ProgressBar progressBarAttractionHomeLoadMore;
    private View viewNoGeolocationError, viewNoAttractionsError, viewMissingLocationPermissionError;
    private Button buttonProvidePermission;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViewComponents(view);
        initializeHomePageFragmentController();
        setListenerOnViewComponents();
        addRecyclerViewOnScrollListener();
        checkDailyReward();
        //setTapTargetSequence();
    }

    private void setTapTargetSequence() {
        homePageFragmentController.setTapTargetSequence();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflateMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("ON REQUEST PERMISSION", "ON REQUEST PERMISSION RESULT");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void inflateMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_attractions:
                startSearchAttractionsActivity();
                break;
        }
    }

    private void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults) {
        homePageFragmentController.onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void initializeViewComponents(@NotNull View view) {
        swipeRefreshLayoutHomeFrament = view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        swipeRefreshLayoutHomeFrament.setRefreshing(true);
        recyclerViewAttractions = view.findViewById(R.id.recycler_view_attraction);
        progressBarAttractionHomeLoadMore = view.findViewById(R.id.progress_bar_attraction_home_load_more);
        viewNoGeolocationError = view.findViewById(R.id.no_geolocation_activated_error_layout);
        viewNoAttractionsError = view.findViewById(R.id.no_attractions_error);
        viewMissingLocationPermissionError = view.findViewById(R.id.missing_location_permission_error_layout);
        buttonProvidePermission = view.findViewById(R.id.button_provide_permission);
    }

    private void initializeHomePageFragmentController() {
        //homePageFragmentController = new HomePageFragmentController(HomePageFragment.this);
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