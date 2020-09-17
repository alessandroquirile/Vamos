package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface ReviewDAO {

    void insert(VolleyCallBack volleyCallBack, Review review, Context context);

    void findHotelReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size);

    void findRestaurantReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size);

    void findAttractionReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size);

}