package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface RestaurantDAO {

    void findByRsql(VolleyCallBack volleyCallBack, List<String> typesOfCuisine, PointSearch pointSearch,
                    String rsqlQuery, Context context, int page, int size);

    void findById(VolleyCallBack volleyCallBack, String id, Context context);

    void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context,
                                  int page, int size);

    void findRestaurantsName(VolleyCallBack volleyCallBack, String name, Context context);

}
