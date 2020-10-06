package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.GPSTracker;
import com.quiriletelese.troppadvisorproject.views.AttractionsListActivity;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.views.RestaurantsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.todkars.shimmer.ShimmerRecyclerView;

import java.util.Arrays;
import java.util.List;

public class HomePageFragmentController implements View.OnClickListener, Constants {

    private HomePageFragment homePageFragment;
    private DAOFactory daoFactory = DAOFactory.getInstance();
    private GPSTracker gpsTracker;
    private RecyclerViewHotelAdapter recyclerViewHotelAdapter;
    private RecyclerViewRestaurantAdapter recyclerViewRestaurantAdapter;
    private RecyclerViewAttractionAdapter recyclerViewAttractionAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PointSearch pointSearch;

    public HomePageFragmentController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
        gpsTracker = createGpsTracker();
    }

    @Override
    public void onClick(View view) {
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
        }
    }

    public void findHotelsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch,
                                       String rsqlQuery) {
        this.pointSearch = pointSearch;
        HotelDAO hotelDAO = getHotelDAO();
        hotelDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), 0, 10);
    }

    public void findRestaurantsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch,
                                            String rsqlQuery) {
        this.pointSearch = pointSearch;
        RestaurantDAO restaurantDAO = getRestaurantDAO();
        restaurantDAO.findByRsql(volleyCallBack, null, pointSearch, rsqlQuery, getContext(),
                0, 10);
    }

    public void findAttractionsByRsqlHelper(VolleyCallBack volleyCallBack, PointSearch pointSearch,
                                            String rsqlQuery) {
        this.pointSearch = pointSearch;
        AttractionDAO attractionDAO = getAttractionDAO();
        attractionDAO.findByRsql(volleyCallBack, pointSearch, rsqlQuery, getContext(), 0, 10);
    }

    public void initializeRecyclerViewHotel(PointSearch pointSearch) {
        findHotelsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewHotelOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewHotelsOnError(errorCode);
            }
        }, pointSearch, "0");
    }

    public void initializeRecyclerViewRestaurant(PointSearch pointSearch) {
        findRestaurantsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewRestaurantOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewRestaurantOnError(errorCode);
            }
        }, pointSearch, "0");
    }

    public void initializeRecyclerViewAttraction(PointSearch pointSearch) {
        findAttractionsByRsqlHelper(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewAttractionOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewAttractionOnError(errorCode);
            }
        }, pointSearch, "0");
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
        return createPointSearch(Arrays.asList(latitude, longitude, 5.0));
    }

    private PointSearch createPointSearch(List<Double> pointSearchInformation) {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(/*pointSearchInformation.get(0)*/40.829904);
        pointSearch.setLongitude(/*pointSearchInformation.get(1)*/14.248052);
        pointSearch.setDistance(/*pointSearchInformation.get(2)*/5.0);
        return pointSearch;
    }

    public void initializeRecyclerViewsFakeContent() {
        linearLayoutManager = createLinearLayoutManager();
        setShimmerRecyclerViewHotelOnStart();
        setShimmerRecyclerViewRestaurantOnStart();
        setShimmerRecyclerViewAttractionOnStart();
    }

    public void setListenerOnViewComponents() {
        getTextViewHotelRecyclerView().setOnClickListener(this);
        getTextViewRestaurantRecyclerView().setOnClickListener(this);
        getTextViewAttractionRecyclerView().setOnClickListener(this);
    }

    private LinearLayoutManager createLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), setRecyclerViewHorizontalOrientation(), false);
    }

    private int setRecyclerViewHorizontalOrientation() {
        return RecyclerView.HORIZONTAL;
    }

    private RecyclerViewHotelAdapter createRecyclerViewHotelAdapter(List<Hotel> hotels) {
        return new RecyclerViewHotelAdapter(getContext(), hotels);
    }

    private RecyclerViewRestaurantAdapter createRecyclerViewRestaurantAdapter(List<Restaurant> restaurants) {
        return new RecyclerViewRestaurantAdapter(getContext(), restaurants);
    }

    private RecyclerViewAttractionAdapter createRecyclerViewAttractionAdapter(List<Attraction> attractions) {
        return new RecyclerViewAttractionAdapter(getContext(), attractions);
    }

    private void runNoHotelsErrorOnUiThread(String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoHotelsErrorVisible);
    }

    private void runNoRestaurantsErrorOnUiThread(String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoRestaurantsErrorVisible);
    }

    private void runNoAttractionsErrorOnUiThread(String errorCode) {
        if (errorCode.equals("204"))
            requireActivity().runOnUiThread(this::setViewNoAttractionsErrorVisible);
    }

    private void startHotelsListActivity() {
        Intent hotelsListActivity = new Intent(homePageFragment.getContext(), HotelsListActivity.class);
        hotelsListActivity.putExtra(POINT_SEARCH, pointSearch);
        homePageFragment.getContext().startActivity(hotelsListActivity);
    }

    private void startRestaurantsListActivity() {
        Intent restaurantsListActivity = new Intent(homePageFragment.getContext(), RestaurantsListActivity.class);
        restaurantsListActivity.putExtra(POINT_SEARCH, pointSearch);
        homePageFragment.getContext().startActivity(restaurantsListActivity);
    }

    private void startAttractionsListActivity() {
        Intent attractionsListActivity = new Intent(homePageFragment.getContext(), AttractionsListActivity.class);
        attractionsListActivity.putExtra(POINT_SEARCH, pointSearch);
        homePageFragment.getContext().startActivity(attractionsListActivity);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(homePageFragment.requireContext(), permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(String[] permission, int requestCode) {
        homePageFragment.requestPermissions(permission, requestCode);
    }

    private Context getContext() {
        return homePageFragment.getContext();
    }

    private FragmentActivity requireActivity() {
        return homePageFragment.requireActivity();
    }

    private GPSTracker createGpsTracker() {
        return new GPSTracker(homePageFragment.getActivity());
    }

    public boolean canGeolocate() {
        return gpsTracker.canGetLocation();
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

    private void setViewNoRestaurantsErrorVisible() {
        getViewNoRestaurantsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoRestaurantsErrorInvisible() {
        getViewNoRestaurantsError().setVisibility(View.GONE);
    }

    private View getViewNoAttractionsError() {
        return homePageFragment.getViewNoAttractionsError();
    }

    private void setViewNoAttractionsErrorVisible() {
        getViewNoAttractionsError().setVisibility(View.VISIBLE);
    }

    private void setViewNoAttractionsErrorInvisible() {
        getViewNoAttractionsError().setVisibility(View.GONE);
    }

}