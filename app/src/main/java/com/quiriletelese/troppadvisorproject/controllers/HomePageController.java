package com.quiriletelese.troppadvisorproject.controllers;

import android.view.View;

import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewAttractionAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewHotelAdapter;
import com.quiriletelese.troppadvisorproject.adapters.RecyclerViewRestaurantAdapter;
import com.quiriletelese.troppadvisorproject.dao_implementations.AttractionDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.HotelDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.RestaurantDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePageController {

    private HomePageFragment homePageFragment;
    private RecyclerViewHotelAdapter recyclerViewHotelAdapter;
    private RecyclerViewRestaurantAdapter recyclerViewRestaurantAdapter;
    private RecyclerViewAttractionAdapter recyclerViewAttractionAdapter;
    private int loaded = 0;

    public HomePageController(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
    }

    public void findHotelsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation){
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        new HotelDAO_MongoDB().findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext());
    }

    public void findRestaurantsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation){
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        new RestaurantDAO_MongoDB().findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext());
    }

    public void findAttractionsByPointNear(VolleyCallBack volleyCallBack, List<Double> pointSearchInformation){
        PointSearch pointSearch = setPointSearchInformation(pointSearchInformation);
        new AttractionDAO_MongoDB().findByPointNear(volleyCallBack, pointSearch, homePageFragment.getContext());
    }

    private PointSearch setPointSearchInformation(List<Double> pointSearchInformation){
        PointSearch pointSearch = new PointSearch();
        pointSearch.setLatitude(pointSearchInformation.get(0));
        pointSearch.setLongitude(pointSearchInformation.get(1));
        pointSearch.setDistance(pointSearchInformation.get(2));
        return pointSearch;
    }

    public void initializeRecyclerViewHotel() {
        findHotelsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                loaded++;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewHotelAdapter = new RecyclerViewHotelAdapter(homePageFragment.getActivity(), accomodation);
                homePageFragment.getRecyclerViewHotel().setLayoutManager(linearLayoutManager);
                homePageFragment.getRecyclerViewHotel().setAdapter(recyclerViewHotelAdapter);
                if(loaded == 3)
                    homePageFragment.getFrameLayout().setVisibility(View.GONE);
            }
            @Override
            public void onError(List accomodation, String error) {

            }
        }, pointSearchInformation());
    }

    public void initializeRecyclerViewRestaurant() {
        findRestaurantsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                loaded++;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewRestaurantAdapter = new RecyclerViewRestaurantAdapter(homePageFragment.getActivity(), accomodation);
                homePageFragment.getRecyclerViewRestaurant().setLayoutManager(linearLayoutManager);
                homePageFragment.getRecyclerViewRestaurant().setAdapter(recyclerViewRestaurantAdapter);
                if(loaded == 3)
                    homePageFragment.getFrameLayout().setVisibility(View.GONE);
            }
            @Override
            public void onError(List accomodation, String error) {

            }
        }, pointSearchInformation());
    }

    public void initializeRecyclerViewAttraction() {
        findAttractionsByPointNear(new VolleyCallBack() {
            @Override
            public void onSuccess(List accomodation) {
                loaded++;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homePageFragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewAttractionAdapter = new RecyclerViewAttractionAdapter(homePageFragment.getActivity(), accomodation);
                homePageFragment.getRecyclerViewAttraction().setLayoutManager(linearLayoutManager);
                homePageFragment.getRecyclerViewAttraction().setAdapter(recyclerViewAttractionAdapter);
                if(loaded == 3)
                    homePageFragment.getFrameLayout().setVisibility(View.GONE);
            }
            @Override
            public void onError(List accomodation, String error) {

            }
        }, pointSearchInformation());
    }

    private List<Double> pointSearchInformation() {
        List<Double> pointSearchInformation = new ArrayList<>();
        pointSearchInformation.add(40.829914);
        pointSearchInformation.add(14.247674);
        pointSearchInformation.add(1.0);
        return pointSearchInformation;
    }

}
