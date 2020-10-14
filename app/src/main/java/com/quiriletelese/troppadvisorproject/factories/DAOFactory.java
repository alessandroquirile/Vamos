package com.quiriletelese.troppadvisorproject.factories;

import com.quiriletelese.troppadvisorproject.dao_implementations.AccountDAO_Cognito;
import com.quiriletelese.troppadvisorproject.dao_implementations.AttractionDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.CityDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.HotelDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.RestaurantDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.ReviewDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.TypeOfCuisineDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.my_exceptions.TechnologyNotSupportedYetException;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class DAOFactory {

    private static DAOFactory instance = null;

    private DAOFactory() {

    }

    public static synchronized DAOFactory getInstance() {
        if (instance == null)
            instance = new DAOFactory();
        return instance;
    }

    public ReviewDAO getReviewDAO(@NotNull String reviewStorageTechnology) {
        if (reviewStorageTechnology.equals("mongodb"))
            return new ReviewDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(reviewStorageTechnology);
    }

    public AccountDAO getAccountDAO(@NotNull String accountStorageTechnology) {
        if (accountStorageTechnology.equals("cognito"))
            return new AccountDAO_Cognito();
        else
            throw new TechnologyNotSupportedYetException(accountStorageTechnology);
    }

    public HotelDAO getHotelDAO(@NotNull String hotelStorageTechnology) {
        if (hotelStorageTechnology.equals("mongodb"))
            return new HotelDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(hotelStorageTechnology);
    }

    public RestaurantDAO getRestaurantDAO(@NotNull String restaurantStorageTechnology) {
        if (restaurantStorageTechnology.equals("mongodb"))
            return new RestaurantDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(restaurantStorageTechnology);
    }

    public AttractionDAO getAttractionDAO(@NotNull String attractionStorageTechnology) {
        if (attractionStorageTechnology.equals("mongodb"))
            return new AttractionDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(attractionStorageTechnology);
    }

    public CityDAO getCityDAO(@NotNull String attractionStorageTechnology) {
        if (attractionStorageTechnology.equals("mongodb"))
            return new CityDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(attractionStorageTechnology);
    }

    public TypeOfCuisineDAO getTypeOfCuisineDAO(@NotNull String attractionStorageTechnology) {
        if (attractionStorageTechnology.equals("mongodb"))
            return new TypeOfCuisineDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(attractionStorageTechnology);
    }

}
