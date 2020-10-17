package com.quiriletelese.troppadvisorproject.controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewRestaurantAdapter;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.GPSTracker;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.todkars.shimmer.ShimmerRecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HomePageFragmentController implements View.OnClickListener, Constants {

    private final HomePageFragment homePageFragment;
    private final DAOFactory daoFactory = DAOFactory.getInstance();
    private final GPSTracker gpsTracker;
    private RecyclerViewHotelAdapter recyclerViewHotelAdapter;
    private RecyclerViewRestaurantAdapter recyclerViewRestaurantAdapter;
    private RecyclerViewAttractionAdapter recyclerViewAttractionAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PointSearch pointSearch;

    public HomePageFragmentController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
        this.gpsTracker = createGpsTracker();
    }

    @Override
    public void onClick(View view) {
        onClickHelper(view);
    }

    public void findHotelsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        getHotelDAO().findByRsql(volleyCallBack, pointSearch, "0", getContext(), 0, 10);
    }

    public void findRestaurantsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        getRestaurantDAO().findByRsql(volleyCallBack, null, pointSearch, "0", getContext(),
                0, 10);
    }

    public void findAttractionsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        getAttractionDAO().findByRsql(volleyCallBack, pointSearch, "0", getContext(), 0, 10);
    }

    public void initializeRecyclerViewHotel() {
        findHotelsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewHotelOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewHotelsOnError(errorCode);
            }
        }, getLocation());
    }

    public void initializeRecyclerViewRestaurant() {
        findRestaurantsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewRestaurantOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewRestaurantOnError(errorCode);
            }
        }, getLocation());
    }

    public void initializeRecyclerViewAttraction() {
        findAttractionsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewAttractionOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewAttractionOnError(errorCode);
            }
        }, getLocation());
    }

    private void initializeRecyclerViewHotelOnSuccess(List<Hotel> hotels) {
        setViewNoHotelsErrorInvisible();
        linearLayoutManager = createLinearLayoutManager();
        recyclerViewHotelAdapter = createRecyclerViewHotelAdapter(hotels);
        setShimmerRecyclerViewHotelOnSuccess();
    }

    private void initializeRecyclerViewHotelsOnError(String errorCode) {
        runNoHotelsErrorOnUiThread(errorCode);
    }

    private void initializeRecyclerViewRestaurantOnSuccess(List<Restaurant> restaurants) {
        setViewNoRestaurantsErrorInvisible();
        linearLayoutManager = createLinearLayoutManager();
        recyclerViewRestaurantAdapter = createRecyclerViewRestaurantAdapter(restaurants);
        setShimmerRecyclerViewRestaurantOnSuccess();
    }

    private void initializeRecyclerViewRestaurantOnError(String errorCode) {
        runNoRestaurantsErrorOnUiThread(errorCode);
    }

    private void initializeRecyclerViewAttractionOnSuccess(List<Attraction> attractions) {
        setViewNoAttractionsErrorInvisible();
        linearLayoutManager = createLinearLayoutManager();
        recyclerViewAttractionAdapter = createRecyclerViewAttractionAdapter(attractions);
        setShimmerRecyclerViewAttractionOnSuccess();
    }

    private void initializeRecyclerViewAttractionOnError(String errorCode) {
        runNoAttractionsErrorOnUiThread(errorCode);
    }

    public PointSearch getLocation() {
        Double latitude = gpsTracker.getLatitude();
        Double longitude = gpsTracker.getLongitude();
        return createPointSearch(Arrays.asList(latitude, longitude));
    }

    @NotNull
    private PointSearch createPointSearch(@NotNull List<Double> pointSearchInformation) {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(5.0);
        return pointSearch;
    }

    public void initializeRecyclerViewsFakeContent() {
        linearLayoutManager = createLinearLayoutManager();
        setShimmerRecyclerViewHotelOnStart();
        setShimmerRecyclerViewRestaurantOnStart();
        setShimmerRecyclerViewAttractionOnStart();
    }

    private void onClickHelper(View view) {
        switch (view.getId()) {
            case R.id.text_view_hotel_recycler_view:
                startHotelsListActivity();
                break;
            case R.id.text_view_restaurant_recycler_view:
                startRestaurantsListActivity();
                break;
            case R.id.text_view_attraction_recycler_view:
                startAttractionsListActivity();
                break;
            case R.id.button_enable_position:
                startEnablePositionActivity();
                break;
            case R.id.button_provide_permission:
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                break;
        }
    }

    public void setListenerOnViewComponents() {
        getTextViewHotelRecyclerView().setOnClickListener(this);
        getTextViewRestaurantRecyclerView().setOnClickListener(this);
        getTextViewAttractionRecyclerView().setOnClickListener(this);
        getButtonEnablePosition().setOnClickListener(this);
        getButtonProvidePermission().setOnClickListener(this);
    }

    public void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION:
                checkPermissionResult(grantResults);
                break;
        }
    }

    private void checkPermissionResult(@NonNull int[] grantResults) {
        if (isPermissionGranted(grantResults)) {
            Log.d("PERMISSION", "LOCATION PERMISSION GRANTED");
            initializeRecyclerViews();
            homePageFragment.getViewMissingLocationPermissionError().setVisibility(View.GONE);
        }
    }

    private void initializeRecyclerViews() {
        homePageFragment.initializeRecyclerViews();
    }

    @NotNull
    @Contract(" -> new")
    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewHorizontalOrientation(), false);
    }

    private int setRecyclerViewHorizontalOrientation() {
        return RecyclerView.HORIZONTAL;
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewHotelAdapter createRecyclerViewHotelAdapter(List<Hotel> hotels) {
        return new RecyclerViewHotelAdapter(getContext(), hotels);
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewRestaurantAdapter createRecyclerViewRestaurantAdapter(List<Restaurant> restaurants) {
        return new RecyclerViewRestaurantAdapter(getContext(), restaurants);
    }

    @NotNull
    @Contract("_ -> new")
    private RecyclerViewAttractionAdapter createRecyclerViewAttractionAdapter(List<Attraction> attractions) {
        return new RecyclerViewAttractionAdapter(getContext(), attractions);
    }

    private void runNoHotelsErrorOnUiThread(@NotNull String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoHotelsErrorVisible);
    }

    private void runNoRestaurantsErrorOnUiThread(@NotNull String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoRestaurantsErrorVisible);
    }

    private void runNoAttractionsErrorOnUiThread(@NotNull String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoAttractionsErrorVisible);
    }

    private void startHotelsListActivity() {
        Intent hotelsListActivityIntent = createHotelsListActivityIntent();
        getContext().startActivity(hotelsListActivityIntent);
    }

    @NotNull
    private Intent createHotelsListActivityIntent() {
        Intent intentHotelsListActivity = new Intent(getContext(), HotelsListActivity.class);
        intentHotelsListActivity.putExtra(POINT_SEARCH, pointSearch);
        intentHotelsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentHotelsListActivity;
    }

    private void startRestaurantsListActivity() {
        Intent restaurantsListActivityIntent = createRestaurantsListActivityIntent();
        getContext().startActivity(restaurantsListActivityIntent);
    }

    @NotNull
    private Intent createRestaurantsListActivityIntent() {
        Intent intentRestaurantsListActivity = new Intent(getContext(), RestaurantsListActivity.class);
        intentRestaurantsListActivity.putExtra(POINT_SEARCH, pointSearch);
        intentRestaurantsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentRestaurantsListActivity;
    }

    private void startAttractionsListActivity() {
        Intent attractionsListActivityIntent = createAttractionsListActivityIntent();
        getContext().startActivity(attractionsListActivityIntent);
    }

    @NotNull
    private Intent createAttractionsListActivityIntent() {
        Intent intentAttractionsListActivity = new Intent(getContext(), AttractionsListActivity.class);
        intentAttractionsListActivity.putExtra(POINT_SEARCH, pointSearch);
        intentAttractionsListActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentAttractionsListActivity;
    }

    private void startEnablePositionActivity() {
        getContext().startActivity(createEnablePositionActivityIntent());
    }

    @NotNull
    @Contract(" -> new")
    private Intent createEnablePositionActivityIntent() {
        return new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(String permission, int requestCode) {
        homePageFragment.requestPermissions(new String[]{permission}, requestCode);
    }

    private Context getContext() {
        return homePageFragment.getContext();
    }

    @NotNull
    private FragmentActivity requireActivity() {
        return homePageFragment.requireActivity();
    }

    @NotNull
    @Contract(" -> new")
    private GPSTracker createGpsTracker() {
        return new GPSTracker(homePageFragment.getActivity());
    }

    public boolean canGeolocate() {
        return createGpsTracker().canGetLocation();
    }

    public boolean areCoordinatesNull() {
        return gpsTracker.getLatitude() == 0 || gpsTracker.getLongitude() == 0;
    }

    private HotelDAO getHotelDAO() {
        return daoFactory.getHotelDAO(getStorageTechnology(HOTEL_STORAGE_TECHNOLOGY));
    }

    private RestaurantDAO getRestaurantDAO() {
        return daoFactory.getRestaurantDAO(getStorageTechnology(RESTAURANT_STORAGE_TECHNOLOGY));
    }

    private AttractionDAO getAttractionDAO() {
        return daoFactory.getAttractionDAO(getStorageTechnology(ATTRACTION_STORAGE_TECHNOLOGY));
    }

    private String getStorageTechnology(String storageTechnology) {
        return ConfigFileReader.getProperty(storageTechnology, getContext());
    }

    private TextView getTextViewHotelRecyclerView() {
        return homePageFragment.getTextViewHotelRecyclerView();
    }

    private TextView getTextViewRestaurantRecyclerView() {
        return homePageFragment.getTextViewRestaurantRecyclerView();
    }

    private TextView getTextViewAttractionRecyclerView() {
        return homePageFragment.getTextViewAttractionRecyclerView();
    }

    private ShimmerRecyclerView getShimmerRecyclerViewHotel() {
        return homePageFragment.getShimmerRecyclerViewHotel();
    }

    private void setShimmerRecyclerViewHotelOnSuccess() {
        getShimmerRecyclerViewHotel().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewHotel().setAdapter(recyclerViewHotelAdapter);
    }

    private void setShimmerRecyclerViewHotelOnStart() {
        getShimmerRecyclerViewHotel().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewHotel().showShimmer();
    }

    private ShimmerRecyclerView getShimmerRecyclerViewRestaurant() {
        return homePageFragment.getShimmerRecyclerViewRestaurant();
    }

    private void setShimmerRecyclerViewRestaurantOnSuccess() {
        getShimmerRecyclerViewRestaurant().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewRestaurant().setAdapter(recyclerViewRestaurantAdapter);
    }

    private void setShimmerRecyclerViewRestaurantOnStart() {
        getShimmerRecyclerViewRestaurant().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewRestaurant().showShimmer();
    }

    private ShimmerRecyclerView getShimmerRecyclerViewAttraction() {
        return homePageFragment.getShimmerRecyclerViewAttraction();
    }

    private void setShimmerRecyclerViewAttractionOnSuccess() {
        getShimmerRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewAttraction().setAdapter(recyclerViewAttractionAdapter);
    }

    private void setShimmerRecyclerViewAttractionOnStart() {
        getShimmerRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
        getShimmerRecyclerViewAttraction().showShimmer();
    }

    private View getViewNoHotelsError() {
        return homePageFragment.getViewNoHotelsError();
    }

    private void setViewNoHotelsErrorVisible() {
        getViewNoHotelsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoHotelsErrorInvisible() {
        getViewNoHotelsError().setVisibility(View.GONE);
    }

    private View getViewNoRestaurantsError() {
        return homePageFragment.getViewNoRestaurantsError();
    }

    private Button getButtonProvidePermission() {
        return homePageFragment.getButtonProvidePermission();
    }

    private void setViewNoRestaurantsErrorVisible() {
        getViewNoRestaurantsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoRestaurantsErrorInvisible() {
        getViewNoRestaurantsError().setVisibility(View.GONE);
    }

    private View getViewNoAttractionsError() {
        return homePageFragment.getViewNoAttractionsError();
    }

    public Button getButtonEnablePosition() {
        return homePageFragment.getButtonEnablePosition();
    }

    private void setViewNoAttractionsErrorVisible() {
        getViewNoAttractionsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoAttractionsErrorInvisible() {
        getViewNoAttractionsError().setVisibility(View.GONE);
    }

    public boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

}