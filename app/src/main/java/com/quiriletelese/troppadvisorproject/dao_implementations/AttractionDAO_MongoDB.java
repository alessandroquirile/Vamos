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
import com.quiriletelese.troppadvisorproject.dao_interfaces.AttractionDAO;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class AttractionDAO_MongoDB implements AttractionDAO, Constants {

    @Override
    public void findByRsql(VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                           Context context, int page, int size) {
        findByRsqlVolley(volleyCallBack, pointSearch, rsqlQuery, context, page, size);
    }

    @Override
    public void findByRsqlNoPoint(VolleyCallBack volleyCallBack, String rsqlQuery, Context context,
                                  int page, int size) {
        findByRsqlNoPointVolley(volleyCallBack, rsqlQuery, context, page, size);
    }

    @Override
    public void findById(VolleyCallBack volleyCallBack, String id, Context context) {
        findByIdVolley(volleyCallBack, id, context);
    }

    @Override
    public void findByNameLikeIgnoreCase(VolleyCallBack volleyCallBack, String name, Context context,
                                         int page, int size) {
        findByNameLikeIgnoreCaseVolley(volleyCallBack, name, context, page, size);
    }

    @Override
    public void findByPointNear(VolleyCallBack volleyCallBack, PointSearch pointSearch, Context context,
                                int page, int size) {
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

    private void findByRsqlVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, String rsqlQuery,
                                  final Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createSearchByRsqlUrl(pointSearch, rsqlQuery, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponse(response));
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByRsqlNoPointVolley(final VolleyCallBack volleyCallBack, String rsqlQuery, Context context,
                                         int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createSearchByRsqlNoPointUrl(rsqlQuery, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponse(response));
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByIdVolley(VolleyCallBack volleyCallBack, String id, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByIdUrl(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getAttractionFromResponse(response));
        }, error -> {

        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findByNameLikeIgnoreCaseVolley(final VolleyCallBack volleyCallBack, String name, Context context,
                                                int page, int size){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createFindByNameLikeIgnoreCaseUrl(name, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponse(response));
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findByPointNearVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, final Context context,
                                       int page, int size) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createFindByPointNearUrl(pointSearch, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponse(response));
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void findAllByPointNearVolley(final VolleyCallBack volleyCallBack, PointSearch pointSearch, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindAllByPointNearUrl(pointSearch);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponseAllAttractions(response));
        }, error -> {

        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private void findHotelsNameVolley(final VolleyCallBack volleyCallBack, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindHotelsNameUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            volleyCallBack.onSuccess(getArrayFromResponseAtractionsName(response));
        }, error -> {

        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    private String createSearchByRsqlUrl(PointSearch pointSearch, String rsqlQuery, int page, int size) {
        String URL = BASE_URL + "/attraction/search-by-rsql?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createSearchByRsqlNoPointUrl(String rsqlQuery, int page, int size) {
        String URL = BASE_URL + "/attraction/search-by-rsql-no-point?";
        URL = URL.concat("query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindByIdUrl(String id) {
        String URL = BASE_URL + "/attraction/find-by-id/";
        URL = URL.concat(id);
        return URL;
    }

    private String createFindByNameLikeIgnoreCaseUrl(String name, int page, int size) {
        String URL = BASE_URL + "/attraction/find-by-name-like-ignore-case?";
        URL = URL.concat("name=" + name);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    @NotNull
    private String createFindByPointNearUrl(@NotNull PointSearch pointSearch, int page, int size) {
        String URL = BASE_URL + "/attraction/find-by-point?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    private String createFindAllByPointNearUrl(PointSearch pointSearch) {
        String URL = BASE_URL + "/attraction/find-all-by-point?";
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        return URL;
    }

    private String createFindHotelsNameUrl(String name) {
        String URL = BASE_URL + "/attraction/find-attraction-name/";
        URL = URL.concat(name);
        return URL;
    }

    private List<Attraction> getArrayFromResponse(@NotNull JSONObject response) {
        List<Attraction> attractions = new ArrayList<>();
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
        return attractions;
    }

    private List<Attraction> getArrayFromResponseAllAttractions(JSONArray response) {
        List<Attraction> attractions = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < response.length(); i++) {
            try {
                attractions.add(gson.fromJson(response.getString(i), Attraction.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return attractions;
    }

    private List<String> getArrayFromResponseAtractionsName(JSONArray response) {
        List<String> attractionsName = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                attractionsName.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return attractionsName;
    }

    private Attraction getAttractionFromResponse(JSONObject response){
        return new Gson().fromJson(response.toString(), Attraction.class);
    }

}
