package com.quiriletelese.troppadvisorproject.factories;

import com.quiriletelese.troppadvisorproject.dao_implementations.AccountDAO_Cognito;
import com.quiriletelese.troppadvisorproject.dao_implementations.AttractionDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.HotelDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.RestaurantDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_implementations.ReviewDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.my_exceptions.TechnologyNotSupportedYetException;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class DAOFactory {

    private static DAOFactory daoFactorySingletonInstance = null;

    private DAOFactory() {

    }

    public static synchronized DAOFactory getInstance() {
        if (daoFactorySingletonInstance == null)
            daoFactorySingletonInstance = new DAOFactory();
        return daoFactorySingletonInstance;
    }

    public ReviewDAO getReviewDAO(String reviewStorageTechnology) {
        if (reviewStorageTechnology.equals("mongodb"))
            return new ReviewDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(reviewStorageTechnology);
    }

    public AccountDAO getAccountDAO(String accountStorageTechnology) {
        if (accountStorageTechnology.equals("cognito"))
            return new AccountDAO_Cognito();
        else
            throw new TechnologyNotSupportedYetException(accountStorageTechnology);
    }

    public HotelDAO getHotelDAO(String hotelStorageTechnology) {
        if (hotelStorageTechnology.equals("mongodb"))
            return new HotelDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(hotelStorageTechnology);
    }

    public RestaurantDAO getRestaurantDAO(String restaurantStorageTechnology) {
        if (restaurantStorageTechnology.equals("mongodb"))
            return new RestaurantDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(restaurantStorageTechnology);
    }

    public AttractionDAO getAttractionDAO(String attractionStorageTechnology) {
        if (attractionStorageTechnology.equals("mongodb"))
            return new AttractionDAO_MongoDB();
        else
            throw new TechnologyNotSupportedYetException(attractionStorageTechnology);
    }
}
