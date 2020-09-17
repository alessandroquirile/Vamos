package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

public interface CityDAO {

    void findCitiesByName(VolleyCallBackCity volleyCallBackCity, String name, Context context);

}
