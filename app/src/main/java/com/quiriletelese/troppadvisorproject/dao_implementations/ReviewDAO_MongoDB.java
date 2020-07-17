package com.quiriletelese.troppadvisorproject.dao_implementations;

import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.models.Review;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class ReviewDAO_MongoDB implements ReviewDAO {

    @Override
    public boolean add(Review review) {
        return false;
        // codice per inserire una recensione su mongodb
    }
}
