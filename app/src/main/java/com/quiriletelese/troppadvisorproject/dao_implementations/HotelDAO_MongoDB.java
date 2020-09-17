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
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class HotelDAO_MongoDB implements HotelDAO {

    private List<Hotel> hotels = new ArrayList<>();
    private List<String> hotelsName = new ArrayList<>();

    @Override
    public void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, Context context, int page, int size) {
        findByRsqlVolley(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    @Override
    public void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, Context context, int page, int size) {
        findByRsqlNoPointVolley(volleyCallBack, rsqlQuery, context, page, size);
    }

    @Override
    public void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context, int page, int size) {
        findByNameLikeIgnoreCaseVolley(volleyCallBack, name, context, page, size);
    }

    @Override
    public void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context, int page, int size) {
        findByPointNearVolley(volleyCallBack, pointSearch, context, page, size);
    }

    @Override
    public void findAllByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context) {
        findAllByPointNearVolley(volleyCallBack, pointSearch, context);
    }

    @Override
    public void findHotelsName(VolleyCallBack volleyCallBack, String name, Context context) {
        findHotelsNameVolley(volleyCallBack, name, context);
    }

    private void findByRsqlVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery, final Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createSearchByRsqlUrl(pointSearch, rsqlQuery, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getArrayFromResponse(response);
                volleyCallBack.onSuccess(hotels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByRsqlNoPointVolley(final VolleyCallBack volleyCallBack, String rsqlQuery, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createSearchByRsqlNoPointUrl(rsqlQuery, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getArrayFromResponse(response);
                volleyCallBack.onSuccess(hotels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByNameLikeIgnoreCaseVolley(final VolleyCallBack volleyCallBack, String name, Context context, int page, int size){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createFindByNameLikeIgnoreCaseUrl(name, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getArrayFromResponse(response);
                volleyCallBack.onSuccess(hotels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByPointNearVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, final Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createFindByPointNearUrl(pointSearch, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getArrayFromResponse(response);
                volleyCallBack.onSuccess(hotels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findAllByPointNearVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindAllByPointNearUrl(pointSearch);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getArrayFromResponseAllHotels(response);
                volleyCallBack.onSuccess(hotels);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void findHotelsNameVolley(final VolleyCallBack volleyCallBack, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindHotelsNameUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getArrayFromResponseHotelsName(response);
                volleyCallBack.onSuccess(hotelsName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private String createSearchByRsqlUrl(PointSearch pointSearch, String rsqlQuery, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/search-by-rsql?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createSearchByRsqlNoPointUrl(String rsqlQuery, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/search-by-rsql-no-point?";
        URL = URL.concat("query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindByNameLikeIgnoreCaseUrl(String name, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/find-by-name-like-ignore-case?";
        URL = URL.concat("name=" + name);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindByPointNearUrl(PointSearch pointSearch, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/find-by-point?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindAllByPointNearUrl(PointSearch pointSearch) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/find-all-by-point?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        return URL;
    }

    private String createFindHotelsNameUrl(String name) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/hotel/find-hotels-name/";
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

    private void getArrayFromResponseAllHotels(JSONArray response) {
        Gson gson = new Gson();
        for (int i = 0; i < response.length(); i++) {
            try {
                hotels.add(gson.fromJson(response.getString(i), Hotel.class));
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

}
