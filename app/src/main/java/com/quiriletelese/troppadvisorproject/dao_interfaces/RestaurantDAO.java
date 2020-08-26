package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public interface RestaurantDAO {

    void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context);

}
