package com.quiriletelese.troppadvisorproject.views;

import android.Manifest;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HomePageFragmentController;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HomePageFragment extends Fragment {

    private HomePageFragmentController homePageFragmentController;
    private RecyclerView recyclerViewAttractions;
    private ProgressBar progressBarAttractionHomeLoadMore;
    private View viewNoGeolocationError, viewNoAttractionsError, viewMissingLocationPermissionError;
    private Button buttonEnablePosition, buttonProvidePermission;

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
        initializeRecyclerView();
        addRecyclerViewOnScrollListener();
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
            case R.id.refresh_button_menu_home_page_activity:
                //initializeRecyclerViews();
                startActivity(new Intent(getActivity(), LeaderboardActivity.class));
                break;
        }
    }

    private void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults) {
        homePageFragmentController.onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void initializeViewComponents(@NotNull View view) {
        recyclerViewAttractions = view.findViewById(R.id.recycler_view_attraction);
        progressBarAttractionHomeLoadMore = view.findViewById(R.id.progress_bar_attraction_home_load_more);
        viewNoGeolocationError = view.findViewById(R.id.no_geolocation_activated_error_layout);
        viewNoAttractionsError = view.findViewById(R.id.no_attractions_error);
        viewMissingLocationPermissionError = view.findViewById(R.id.missing_location_permission_error_layout);
        buttonEnablePosition = view.findViewById(R.id.button_enable_position);
        buttonProvidePermission = view.findViewById(R.id.button_provide_permission);
    }

    private void initializeHomePageFragmentController() {
        homePageFragmentController = new HomePageFragmentController(HomePageFragment.this);
    }

    private void setListenerOnViewComponents() {
        homePageFragmentController.setListenerOnViewComponents();
    }

    private void addRecyclerViewOnScrollListener() {
        homePageFragmentController.addRecyclerViewOnScrollListener();
    }

    private boolean checkPermission() {
        return homePageFragmentController.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void initializeRecyclerView() {
        if (checkPermission()) {
            if (canGeolocate()) {
                setViewNoGeolocationErrorVisibility(View.INVISIBLE);
                do {
                    if (!areCoordinatesNull())
                        initializeRecyclerViewAttraction();
                } while (areCoordinatesNull());
            } else
                setViewNoGeolocationErrorVisibility(View.VISIBLE);
        } else
            setViewMissingLocationPermissionErrorVisibility(View.VISIBLE);
    }

    private void initializeRecyclerViewAttraction() {
        homePageFragmentController.findByRsql();
    }

    private void setViewNoGeolocationErrorVisibility(int visibility) {
        viewNoGeolocationError.setVisibility(visibility);
    }

    private void setViewMissingLocationPermissionErrorVisibility(int visibility) {
        viewMissingLocationPermissionError.setVisibility(visibility);
    }

    private boolean canGeolocate() {
        return homePageFragmentController.canGeolocate();
    }

    private boolean areCoordinatesNull() {
        return homePageFragmentController.areCoordinatesNull();
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

    public Button getButtonEnablePosition() {
        return buttonEnablePosition;
    }

    public Button getButtonProvidePermission() {
        return buttonProvidePermission;
    }

    public View getViewMissingLocationPermissionError() {
        return viewMissingLocationPermissionError;
    }
}