package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.HotelDAO;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Point;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelDAO_MongoDB implements HotelDAO, Constants {

    private List<Hotel> hotels = new ArrayList<>();
    private List<String> hotelsName = new ArrayList<>();

    @Override
    public void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, Context context, int page, int size) {
        findByRsqlVolley(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    @Override
    public void findById(VolleyCallBack volleyCallBack, String id, Context context) {
        findByIdVolley(volleyCallBack, id, context);
    }

    @Override
    public void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context, int page, int size) {
        findByNameLikeIgnoreCaseVolley(volleyCallBack, name, context, page, size);
    }

    @Override
    public void findHotelsName(VolleyCallBack volleyCallBack, String name, Context context) {
        findHotelsNameVolley(volleyCallBack, name, context);
    }

    private void findByRsqlVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, final Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createSearchByRsqlUrl(pointSearch, rsqlQuery, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponse(response);
            volleyCallBack.onSuccess(hotels);
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findByIdVolley(VolleyCallBack volleyCallBack, String id, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByIdUrl(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getHotelFromResponse(response));
        }, error -> {

        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findByNameLikeIgnoreCaseVolley(final VolleyCallBack volleyCallBack, String name, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByNameLikeIgnoreCaseUrl(name, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponse(response);
            volleyCallBack.onSuccess(hotels);
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findHotelsNameVolley(final VolleyCallBack volleyCallBack, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindHotelsNameUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponseHotelsName(response);
            volleyCallBack.onSuccess(hotelsName);
        }, error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonArrayRequest);
    }

    private String createSearchByRsqlUrl(PointSearch pointSearch, String rsqlQuery, int page, int size) {
        String URL = BASE_URL + "hotel/search-by-rsql?";
        if (pointSearch != null)
            URL = createStringSearchByRsqlUrlWithPointSearch(URL, pointSearch, rsqlQuery, page, size);
        else
            URL = createStringSearchByRsqlUrlNoPointSearch(URL, rsqlQuery, page, size);

        return URL;
    }

    private String createStringSearchByRsqlUrlNoPointSearch(String URL, String rsqlQuery, int page, int size){
        URL = URL.concat("query=" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }
    private String createStringSearchByRsqlUrlWithPointSearch(String URL, PointSearch pointSearch,
                                                              String rsqlQuery, int page, int size){
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindByIdUrl(String id) {
        String URL = BASE_URL + "hotel/find-by-id/";
        URL = URL.concat(id);
        return URL;
    }

    private String createFindByNameLikeIgnoreCaseUrl(String name, int page, int size) {
        String URL = BASE_URL + "hotel/find-by-name-like-ignore-case?";
        URL = URL.concat("name=" + name);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindHotelsNameUrl(String name) {
        String URL = BASE_URL + "hotel/find-hotels-name/";
        URL = URL.concat(name);
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
                hotels.add(gson.fromJson(jsonArray.getString(i), Hotel.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getArrayFromResponseHotelsName(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                hotelsName.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Hotel getHotelFromResponse(JSONObject response){
        return new Gson().fromJson(response.toString(), Hotel.class);
    }

    private boolean isStatusCodeOk(int statusCode){
        return statusCode == 200;
    }

}
