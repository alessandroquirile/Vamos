package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.Arrays;
import java.util.List;

public class HomePageFragmentController implements View.OnClickListener, Constants {

    private HomePageFragment homePageFragment;
    private DAOFactory daoFactory;
    private GPSTracker gpsTracker;
    private RecyclerViewHotelAdapter recyclerViewHotelAdapter;
    private RecyclerViewRestaurantAdapter recyclerViewRestaurantAdapter;
    private RecyclerViewAttractionAdapter recyclerViewAttractionAdapter;
    private PointSearch pointSearch;

    public HomePageFragmentController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
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

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty("hotel_storage_technology", homePageFragment.requireActivity()));
        hotelDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    public void findRestaurantsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty("restaurant_storage_technology", homePageFragment.requireActivity()));
        restaurantDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    public void findAttractionsByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch) {
        this.pointSearch = pointSearch;
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty("attraction_storage_technology", homePageFragment.requireActivity()));
        attractionDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    public void initializeRecyclerViewHotel(PointSearch pointSearch) {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewHotelOnSuccess((List<Hotel>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewHotelOnError(errorCode);
            }
        }, pointSearch);
    }

    private void initializeRecyclerViewHotelOnSuccess(List<Hotel> hotels) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHotelAdapter = new RecyclerViewHotelAdapter(homePageFragment.getActivity(), hotels);
        homePageFragment.getShimmerRecyclerViewHotel().setLayoutManager(linearLayoutManager);
        homePageFragment.getShimmerRecyclerViewHotel().setAdapter(recyclerViewHotelAdapter);
    }

    private void initializeRecyclerViewHotelOnError(String errorCode) {
        if (errorCode.equals("204"))
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun hotel trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void initializeRecyclerViewRestaurant(PointSearch pointSearch) {
        findRestaurantsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewRestaurantOnSuccess((List<Restaurant>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewRestaurantOnError(errorCode);
            }
        }, pointSearch);
    }

    private void initializeRecyclerViewRestaurantOnSuccess(List<Restaurant> restaurants) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRestaurantAdapter = new RecyclerViewRestaurantAdapter(homePageFragment.getActivity(), restaurants);
        homePageFragment.getShimmerRecyclerViewRestaurant().setLayoutManager(linearLayoutManager);
        homePageFragment.getShimmerRecyclerViewRestaurant().setAdapter(recyclerViewRestaurantAdapter);
    }

    private void initializeRecyclerViewRestaurantOnError(String errorCode) {
        if (errorCode.equals("204"))
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun ristorante trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void initializeRecyclerViewAttraction(PointSearch pointSearch) {
        findAttractionsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(Object object) {
                initializeRecyclerViewAttractionOnSuccess((List<Attraction>) object);
            }

            @Override
            public void onError(String errorCode) {
                initializeRecyclerViewAttractionOnError(errorCode);
            }
        }, pointSearch);
    }

    private void initializeRecyclerViewAttractionOnSuccess(List<Attraction> attractions) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAttractionAdapter = new RecyclerViewAttractionAdapter(homePageFragment.getActivity(), attractions);
        homePageFragment.getShimmerRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
        homePageFragment.getShimmerRecyclerViewAttraction().setAdapter(recyclerViewAttractionAdapter);
    }

    private void initializeRecyclerViewAttractionOnError(String errorCode) {
        if (errorCode.equals("204"))
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun attrazione trovato nelle vicinanze",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    public PointSearch getLocation() {
        gpsTracker = new GPSTracker(homePageFragment.getActivity());
        if (gpsTracker.canGetLocation()) {
            Double latitude = gpsTracker.getLatitude();
            Double longitude = gpsTracker.getLongitude();
            return createPointSearch(Arrays.asList(latitude, longitude, 1.0));
        } else {
            Toast.makeText(homePageFragment.getContext(), "NO", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private PointSearch createPointSearch(List<Double> pointSearchInformation) {
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(/*pointSearchInformation.get(0)*/40.829904);
        pointSearch.setLongitude(/*pointSearchInformation.get(1)*/14.248052);
        pointSearch.setDistance(/*pointSearchInformation.get(2)*/5.0);
        return pointSearch;
    }

    public void initializeRecyclerViewsFakeContent(){
        homePageFragment.getShimmerRecyclerViewHotel().setLayoutManager(new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false) );
        homePageFragment.getShimmerRecyclerViewRestaurant().setLayoutManager(new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        homePageFragment.getShimmerRecyclerViewAttraction().setLayoutManager(new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        homePageFragment.getShimmerRecyclerViewHotel().showShimmer();
        homePageFragment.getShimmerRecyclerViewRestaurant().showShimmer();
        homePageFragment.getShimmerRecyclerViewAttraction().showShimmer();


    }

    public void setListenerOnViewComponents() {
        homePageFragment.getTextViewHotelRecyclerView().setOnClickListener(this);
        homePageFragment.getTextViewRestaurantRecyclerView().setOnClickListener(this);
        homePageFragment.getTextViewAttractionRecyclerView().setOnClickListener(this);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(homePageFragment.requireContext(), permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(String[] permission, int requestCode) {
        homePageFragment.requestPermissions(permission, requestCode);
    }

}
