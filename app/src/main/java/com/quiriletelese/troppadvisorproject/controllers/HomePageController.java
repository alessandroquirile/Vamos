package com.quiriletelese.troppadvisorproject.controllers;

import androidx.paging.PagedList;

import com.quiriletelese.troppadvisorproject.dao_implementations.AttractionDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.HotelDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.RestaurantDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.views.HomePageFragment;

import java.util.List;

public class HomePageController {

    private HomePageFragment homePageFragment;

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

}
