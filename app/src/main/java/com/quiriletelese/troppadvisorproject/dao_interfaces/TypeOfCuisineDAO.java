package com.quiriletelese.troppadvisorproject.dao_interfaces;

import android.content.Context;

import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

public interface TypeOfCuisineDAO {

    void getAll(VolleyCallBack volleyCallBack, Context context);

}
