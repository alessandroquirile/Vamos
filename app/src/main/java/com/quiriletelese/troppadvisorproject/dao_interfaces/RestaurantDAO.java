package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface RestaurantDAO {

    void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, Context context, int page, int size);

    void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, Context context, int page, int size);

    void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context, int page, int size);

    void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context, int page, int size);

    void findAllByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context);

    void findRestaurantsName(VolleyCallBack volleyCallBack, String name, Context context);

}
