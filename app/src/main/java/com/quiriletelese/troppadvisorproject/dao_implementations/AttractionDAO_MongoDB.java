package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallBack;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class AttractionDAO_MongoDB implements AttractionDAO {

    private List<Attraction> attractions = new ArrayList<>();

    @Override
    public void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context) {
        findByPointNearVolley(volleyCallBack, pointSearch, context);
    }

    private void findByPointNearVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, final Context context) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createFindAttractionsByDistanceString(pointSearch);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getArrayFromResponse(response);
                volleyCallBack.onSuccess(attractions);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(null, String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private String createFindAttractionsByDistanceString(PointSearch pointSearch) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/attraction/find-by-point?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&page=0&size=10");
        return URL;
    }

    private void getArrayFromResponse(JSONObject response) {
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        try {
            jsonArray = response.getJSONArray("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                attractions.add(gson.fromJson(jsonArray.getString(i), Attraction.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
