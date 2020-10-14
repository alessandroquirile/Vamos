package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public interface CityDAO {

    void findCitiesByName(VolleyCallBack volleyCallBack, String name, Context context);

}
