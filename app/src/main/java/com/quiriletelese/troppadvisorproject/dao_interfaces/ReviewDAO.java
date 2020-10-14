package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface ReviewDAO {

    void insertHotelReview(VolleyCallBack volleyCallBack, Review review, Context context);

    void insertRestaurantReview(VolleyCallBack volleyCallBack, Review review, Context context);

    void insertAttractionReview(VolleyCallBack volleyCallBack, Review review, Context context);

    void findAccomodationReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size);

}