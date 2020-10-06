package com.quiriletelese.troppadvisorproject.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.HomePageFragmentController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.todkars.shimmer.ShimmerRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomePageFragment extends Fragment implements Constants {

    private static final int ACCESS_FINE_LOCATION = 100;
    private HomePageFragmentController homePageFragmentController;
    private ShimmerRecyclerView shimmerRecyclerViewHotel, shimmerRecyclerViewRestaurant, shimmerRecyclerViewAttraction;
    private TextView textViewHotelRecyclerView, textViewRestaurantRecyclerView, textViewAttractionRecyclerView;
    private View viewNoGeolocationError, viewNoHotelsError, viewNoRestaurantsError, viewNoAttractionsError;
    private List<Double> pointSearchArguments;
    private boolean ok = false;

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
        initializeRecyclerViews();
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

    private void inflateMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.main_menu, menu);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_button_menu_home_page_activity:
                initializeRecyclerViews();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeRecyclerViewHotel();
                initializeRecyclerViewRestaurant();
                initializeRecyclerViewAttraction();
            } else
                Toast.makeText(getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViewComponents(@NotNull View view) {
        shimmerRecyclerViewHotel = view.findViewById(R.id.recycler_view_hotel);
        shimmerRecyclerViewRestaurant = view.findViewById(R.id.recycler_view_restaurant);
        shimmerRecyclerViewAttraction = view.findViewById(R.id.recycler_view_attraction);
        textViewHotelRecyclerView = view.findViewById(R.id.text_view_hotel_recycler_view);
        textViewRestaurantRecyclerView = view.findViewById(R.id.text_view_restaurant_recycler_view);
        textViewAttractionRecyclerView = view.findViewById(R.id.text_view_attraction_recycler_view);
        viewNoGeolocationError = view.findViewById(R.id.no_geolocation_activated_error_layout);
        viewNoHotelsError = view.findViewById(R.id.no_hotels_error);
        viewNoRestaurantsError = view.findViewById(R.id.no_restaurants_error);
        viewNoAttractionsError = view.findViewById(R.id.no_attractions_error);
    }

    private void initializeHomePageFragmentController() {
        homePageFragmentController = new HomePageFragmentController(HomePageFragment.this);
    }

    private void setListenerOnViewComponents() {
        homePageFragmentController.setListenerOnViewComponents();
        ;
    }

    private void initializeRecyclerViewsFakeContent() {
        homePageFragmentController.initializeRecyclerViewsFakeContent();
    }

    private boolean checkPermission() {
        boolean isGranted = true;
        if (homePageFragmentController.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && homePageFragmentController.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            isGranted = false;
            homePageFragmentController.requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_FINE_LOCATION);
        }
        return isGranted;
    }

    private void initializeRecyclerViews() {
        if (checkPermission()) {
            //pointSearchArguments = homePageController.getLocation();
            Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
            if (canGeolocate()) {
                do {
                    if (!areCoordinatesNull()) {
                        initializeRecyclerViewHotel();
                        initializeRecyclerViewRestaurant();
                        initializeRecyclerViewAttraction();
                        initializeRecyclerViewsFakeContent();
                    }
                } while (areCoordinatesNull());
            } else
                setViewNoGeolocationErrorVisible();
        } else
            Toast.makeText(getContext(), "NON GRANTED", Toast.LENGTH_SHORT).show();
    }

    private void initializeRecyclerViewHotel() {
        homePageFragmentController.initializeRecyclerViewHotel(homePageFragmentController.getLocation());
    }

    private void initializeRecyclerViewRestaurant() {
        homePageFragmentController.initializeRecyclerViewRestaurant(homePageFragmentController.getLocation());
    }

    private void initializeRecyclerViewAttraction() {
        homePageFragmentController.initializeRecyclerViewAttraction(homePageFragmentController.getLocation());
    }

    private void setViewNoGeolocationErrorVisible() {
        viewNoGeolocationError.setVisibility(View.VISIBLE);
    }

    private boolean canGeolocate() {
        return homePageFragmentController.canGeolocate();
    }

    private boolean areCoordinatesNull() {
        return homePageFragmentController.areCoordinatesNull();
    }

    public ShimmerRecyclerView getShimmerRecyclerViewHotel() {
        return shimmerRecyclerViewHotel;
    }

    public ShimmerRecyclerView getShimmerRecyclerViewRestaurant() {
        return shimmerRecyclerViewRestaurant;
    }

    public ShimmerRecyclerView getShimmerRecyclerViewAttraction() {
        return shimmerRecyclerViewAttraction;
    }

    public TextView getTextViewHotelRecyclerView() {
        return textViewHotelRecyclerView;
    }

    public TextView getTextViewRestaurantRecyclerView() {
        return textViewRestaurantRecyclerView;
    }

    public TextView getTextViewAttractionRecyclerView() {
        return textViewAttractionRecyclerView;
    }

    public View getViewNoHotelsError() {
        return viewNoHotelsError;
    }

    public View getViewNoRestaurantsError() {
        return viewNoRestaurantsError;
    }

    public View getViewNoAttractionsError() {
        return viewNoAttractionsError;
    }

}