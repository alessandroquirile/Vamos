package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

public interface CityDAO {

    void findCitiesByName(VolleyCallBack volleyCallBack, String name, Context context);

}
