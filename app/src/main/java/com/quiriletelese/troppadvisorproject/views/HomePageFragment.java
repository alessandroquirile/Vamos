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
        //initializeRecyclerView();
        addRecyclerViewOnScrollListener();

//        new TapTargetSequence(getActivity())
//                .targets(
//                        create(view),
//                        TapTarget.forView(view.findViewById(R.id.recycler_view_attraction), "You", "Up")
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.colorPrimary)
//                                .targetCircleColor(R.color.white)
//                                .textColor(android.R.color.white)
//                                .targetCircleColor(R.color.white)
//                                .drawShadow(true)                    // Whether to draw a drop shadow or not
//                                .cancelable(false)
//                                .tintTarget(false)                   // Specify whether the target is transparent (displays the content underneath)
//                                .targetRadius(100))
//                .listener(new TapTargetSequence.Listener() {
//                    // This listener will tell us when interesting(tm) events happen in regards
//                    // to the sequence
//                    @Override
//                    public void onSequenceFinish() {
//                        Toast.makeText(getActivity(), "FINISHHHHHHHHHH", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                        // Perform action for the current target
//                    }
//
//                    @Override
//                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        // Boo
//                    }
//                }).start();
    }

//    private TapTarget create(View view) {
//        return TapTarget.forView(view.findViewById(R.id.prova), "Gonna")
//                .tintTarget(false)
//                .dimColor(android.R.color.black)
//                .outerCircleColor(R.color.colorPrimary)
//                .targetCircleColor(R.color.white)
//                .textColor(android.R.color.white)
//                .targetCircleColor(R.color.white)
//                .drawShadow(true)
//                .cancelable(false)
//                .tintTarget(false)
//                .targetRadius(100);
//    }

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
                //startSearchAttractionsActivity();
                startActivity(new Intent(this.getActivity(), ResetPasswordActivity.class));
                break;
            /*case R.id.refresh_button_menu_home_page_activity:
                initializeRecyclerView();
                break;*/
        }
    }

    private void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults) {
        homePageFragmentController.onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void initializeViewComponents(@NotNull View view) {
        swipeRefreshLayoutHomeFrament = view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        swipeRefreshLayoutHomeFrament.setRefreshing(true);
        recyclerViewAttractions = view.findViewById(R.id.recycler_view_attraction);
        //progressBarHome = view.findViewById(R.id.progress_bar_home);
        progressBarAttractionHomeLoadMore = view.findViewById(R.id.progress_bar_attraction_home_load_more);
        viewNoGeolocationError = view.findViewById(R.id.no_geolocation_activated_error_layout);
        viewNoAttractionsError = view.findViewById(R.id.no_attractions_error);
        viewMissingLocationPermissionError = view.findViewById(R.id.missing_location_permission_error_layout);
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

    private void startSearchAttractionsActivity() {
        homePageFragmentController.startAttractionsListActivity();
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

    public View getViewMissingLocationPermissionError() {
        return viewMissingLocationPermissionError;
    }
}