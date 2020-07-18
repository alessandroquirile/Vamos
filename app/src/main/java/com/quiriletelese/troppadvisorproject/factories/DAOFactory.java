package com.quiriletelese.troppadvisorproject.factories;

import com.quiriletelese.troppadvisorproject.dao_implementations.AccountDAO_Cognito;
import com.quiriletelese.troppadvisorproject.dao_implementations.ReviewDAO_MongoDB;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;

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
            throw new UnsupportedOperationException("Tecnologia " + reviewStorageTechnology + "non ancora supportata");
    }

    public AccountDAO getAccountDAO(String accountStorageTechnology) {
        if (accountStorageTechnology.equals("cognito"))
            return new AccountDAO_Cognito();
        else
            throw new UnsupportedOperationException("Tecnologia " + accountStorageTechnology + "non ancora supportata");
    }
}
