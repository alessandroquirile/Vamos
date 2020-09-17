package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.quiriletelese.troppadvisorproject.dao_interfaces.CityDAO;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBackCity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CityDAO_MongoDB implements CityDAO {

    private List<String> citiesName = new ArrayList<>();

    @Override
    public void findCitiesByName(VolleyCallBackCity volleyCallBackCity, String name, Context context) {
        findCitiesNameVolley(volleyCallBackCity, name, context);
    }

    private void findCitiesNameVolley(final VolleyCallBackCity volleyCallBackCity, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindCitiesUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getArrayFromResponseCitiesName(response);
                volleyCallBackCity.onSuccess(citiesName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getArrayFromResponseCitiesName(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                citiesName.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String createFindCitiesUrl(String name) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/city/find-cities-by-name-like/";
        URL = URL.concat(name);
        return URL;
    }

}
