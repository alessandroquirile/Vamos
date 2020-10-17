package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class CityDAO_MongoDB implements CityDAO, Constants {

    @Override
    public void findCitiesByName(VolleyCallBack volleyCallBack, String name, Context context) {
        findCitiesNameVolley(volleyCallBack, name, context);
    }

    private void findCitiesNameVolley(final VolleyCallBack volleyCallBack, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindCitiesUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponseCitiesName(response);
            volleyCallBack.onSuccess(getArrayFromResponseCitiesName(response));
        }, error -> {

        });
        requestQueue.add(jsonArrayRequest);
    }

    @NotNull
    private List<String> getArrayFromResponseCitiesName(@NotNull JSONArray response) {
        List<String> citiesName = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                citiesName.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return citiesName;
    }

    @NotNull
    private String createFindCitiesUrl(String name) {
        String URL = BASE_URL + "city/find-cities-by-name-like/";
        URL = URL.concat(name);
        return URL;
    }

}
