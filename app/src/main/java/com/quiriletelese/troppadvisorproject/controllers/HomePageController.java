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
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.utils.GPSTracker;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;
import com.quiriletelese.troppadvisorproject.views.HotelsListActivity;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomePageController implements View.OnClickListener, Constants {

    private HomePageFragment homePageFragment;
    private DAOFactory daoFactory;
    private GPSTracker gpsTracker;
    private RecyclerViewHotelAdapter recyclerViewHotelAdapter;
    private RecyclerViewRestaurantAdapter recyclerViewRestaurantAdapter;
    private RecyclerViewAttractionAdapter recyclerViewAttractionAdapter;
    private PointSearch pointSearch;

    public HomePageController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_view_hotel_recycler_view:
                Intent intent = new Intent(homePageFragment.getContext(), HotelsListActivity.class);
                intent.putExtra(POINT_SEARCH, pointSearch);
                homePageFragment.getContext().startActivity(new Intent(homePageFragment.getContext(), HotelsListActivity.class));
                break;

        }
    }

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation) {
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        daoFactory = DAOFactory.getInstance();
        HotelDAO hotelDAO = daoFactory.getHotelDAO(ConfigFileReader.getProperty("hotel_storage_technology", homePageFragment.requireActivity()));
        hotelDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    public void findRestaurantsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation) {
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        daoFactory = DAOFactory.getInstance();
        RestaurantDAO restaurantDAO = daoFactory.getRestaurantDAO(ConfigFileReader.getProperty("restaurant_storage_technology", homePageFragment.requireActivity()));
        restaurantDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    public void findAttractionsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation) {
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        daoFactory = DAOFactory.getInstance();
        AttractionDAO attractionDAO = daoFactory.getAttractionDAO(ConfigFileReader.getProperty("attraction_storage_technology", homePageFragment.requireActivity()));
        attractionDAO.findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext(), 0, 10);
    }

    private PointSearch setPointSearchInformation(@NotNull List<Double> pointSearchInformation) {
        pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(pointSearchInformation.get(2));
        return pointSearch;
    }

    public void initializeRecyclerViewHotel(List<Double> pointSearchArguments) {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                initializeRecyclerViewHotelOnSuccess(accomodation);
            }

            @Override
            public void onError(List accomodation, String error) {
                initializeRecyclerViewHotelOnError(accomodation);
            }
        }, pointSearchInformation(pointSearchArguments));
    }

    private void initializeRecyclerViewHotelOnSuccess(List accomodation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHotelAdapter = new RecyclerViewHotelAdapter(homePageFragment.getActivity(), accomodation);
        homePageFragment.getRecyclerViewHotel().setLayoutManager(linearLayoutManager);
        homePageFragment.getRecyclerViewHotel().setAdapter(recyclerViewHotelAdapter);
    }

    private void initializeRecyclerViewHotelOnError(List accomodation) {
        if (accomodation == null)
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun hotel trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void initializeRecyclerViewRestaurant(List<Double> pointSearchArguments) {
        findRestaurantsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                initializeRecyclerViewRestaurantOnSuccess(accomodation);
            }

            @Override
            public void onError(List accomodation, String error) {
                initializeRecyclerViewRestaurantOnError(accomodation);
            }
        }, pointSearchInformation(pointSearchArguments));
    }

    private void initializeRecyclerViewRestaurantOnSuccess(List accomodation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRestaurantAdapter = new RecyclerViewRestaurantAdapter(homePageFragment.getActivity(), accomodation);
        homePageFragment.getRecyclerViewRestaurant().setLayoutManager(linearLayoutManager);
        homePageFragment.getRecyclerViewRestaurant().setAdapter(recyclerViewRestaurantAdapter);
    }

    private void initializeRecyclerViewRestaurantOnError(List accomodation) {
        if (accomodation == null)
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun ristorante trovato nelle vicinanze", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void initializeRecyclerViewAttraction(List<Double> pointSearchArguments) {
        findAttractionsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                initializeRecyclerViewAttractionOnSuccess(accomodation);
            }

            @Override
            public void onError(List accomodation, String error) {
                initializeRecyclerViewAttractionOnError(accomodation);
            }
        }, pointSearchInformation(pointSearchArguments));
    }

    private void initializeRecyclerViewAttractionOnSuccess(List accomodation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAttractionAdapter = new RecyclerViewAttractionAdapter(homePageFragment.getActivity(), accomodation);
        homePageFragment.getRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
        homePageFragment.getRecyclerViewAttraction().setAdapter(recyclerViewAttractionAdapter);
    }

    private void initializeRecyclerViewAttractionOnError(List accomodation) {
        if (accomodation == null)
            homePageFragment.requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(homePageFragment.getContext(), "Nessun attrazione trovato nelle vicinanze",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    public List<Double> getLocation() {
        gpsTracker = new GPSTracker(homePageFragment.getActivity());
        if (gpsTracker.canGetLocation()) {
            Double latitude = gpsTracker.getLatitude();
            Double longitude = gpsTracker.getLongitude();
            return createPointSearchArguments(latitude, longitude, 1.0);
        } else {
            Toast.makeText(homePageFragment.getContext(), "NO", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private List<Double> createPointSearchArguments(Double latitude, Double longitude, Double distance) {
        List<Double> pointSearchArguments = new ArrayList<>();
        pointSearchArguments.add(latitude);
        pointSearchArguments.add(longitude);
        pointSearchArguments.add(distance);
        return pointSearchArguments;
    }

    private List<Double> pointSearchInformation(List<Double> pointSearchArguments) {
        List<Double> pointSearchInformation = new ArrayList<>();
        pointSearchInformation.add(40.829904);
        pointSearchInformation.add(14.248052);
        pointSearchInformation.add(1.0);
        return pointSearchInformation;
    }

    public void setListenerOnViewComponents(){
        homePageFragment.getTextViewHotelRecyclerView().setOnClickListener(this);
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(homePageFragment.requireContext(), permission) == PackageManager.PERMISSION_DENIED;
    }

    public void requestPermission(String[] permission, int requestCode) {
        homePageFragment.requestPermissions(permission, requestCode);
    }

}
