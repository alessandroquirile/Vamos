package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.RestaurantDAO;
import com.quiriletelese.troppadvisorproject.model_helpers.CustomJsonObjectRequest;
import com.quiriletelese.troppadvisorproject.model_helpers.PointSearch;
import com.quiriletelese.troppadvisorproject.models.Restaurant;
import com.quiriletelese.troppadvisorproject.util_interfaces.Constants;
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

public class RestaurantDAO_MongoDB implements RestaurantDAO, Constants {

    @Override
    public void findByRsql(VolleyCallBack volleyCallBack, List<String> typesOfCuisine, PointSearch pointSearch,
                           String rsqlQuery, Context context, int page, int size) {
        findByRsqlVolley(volleyCallBack, typesOfCuisine, pointSearch, rsqlQuery, context, page, size);
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
    public void findRestaurantsName(VolleyCallBack volleyCallBack, String name, Context context) {
        findRestaurantsNameVolley(volleyCallBack, name, context);
    }

    private void findByRsqlVolley(final VolleyCallBack volleyCallBack, List<String> typesOfCuisine,
                                  PointSearch pointSearch, String rsqlQuery, final Context context,
                                  int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createSearchByRsqlUrl(pointSearch, rsqlQuery, page, size);
        JSONArray jsonObjectTypesOfCuisine = typesOfCuisine == null ? jsonObjectTypesOfCuisine(new ArrayList<>())
                : jsonObjectTypesOfCuisine(typesOfCuisine);
        CustomJsonObjectRequest customJsonObjectRequest = new CustomJsonObjectRequest(Request.Method.POST, URL, jsonObjectTypesOfCuisine, response -> volleyCallBack.onSuccess(getArrayFromResponse(response)), error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(customJsonObjectRequest);
    }

    private void findByIdVolley(VolleyCallBack volleyCallBack, String id, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByIdUrl(id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response ->
                volleyCallBack.onSuccess(getRestaurantFromResponse(response)), error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findRestaurantsNameVolley(final VolleyCallBack volleyCallBack, String name, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindRestaurantsNameUrl(name);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response ->
                volleyCallBack.onSuccess(getArrayFromResponseRestaurantsName(response)), error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonArrayRequest);
    }

    private void findByNameLikeIgnoreCaseVolley(final VolleyCallBack volleyCallBack, String name, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByNameLikeIgnoreCaseUrl(name, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response ->
                volleyCallBack.onSuccess(getArrayFromResponse(response)), error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private String createSearchByRsqlUrl(PointSearch pointSearch, String rsqlQuery, int page, int size) {
        String URL = BASE_URL + "restaurant/search-by-rsql?";
        if (pointSearch != null)
            URL = createStringSearchByRsqlUrlWithPointSearch(URL, pointSearch, rsqlQuery, page, size);
        else
            URL = createStringSearchByRsqlUrlNoPointSearch(URL, rsqlQuery, page, size);
        return URL;
    }

    @NotNull
    private String createStringSearchByRsqlUrlNoPointSearch(String URL, String rsqlQuery, int page, int size) {
        URL = URL.concat("query=" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    @NotNull
    private String createStringSearchByRsqlUrlWithPointSearch(String URL, @NotNull PointSearch pointSearch,
                                                              String rsqlQuery, int page, int size) {
        URL = URL.concat("latitude=" + pointSearch.getLatitude());
        URL = URL.concat("&longitude=" + pointSearch.getLongitude());
        URL = URL.concat("&distance=" + pointSearch.getDistance());
        URL = URL.concat("&query=" + "" + rsqlQuery);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    @NotNull
    private String createFindByIdUrl(String id) {
        String URL = BASE_URL + "restaurant/find-by-id/";
        URL = URL.concat(id);
        return URL;
    }

    @NotNull
    private String createFindByNameLikeIgnoreCaseUrl(String name, int page, int size) {
        String URL = BASE_URL + "restaurant/find-by-name-like-ignore-case?";
        URL = URL.concat("name=" + name);
        URL = URL.concat("&page=" + page + "&size=" + size);
        return URL;
    }

    @NotNull
    private String createFindRestaurantsNameUrl(String name) {
        String URL = BASE_URL + "restaurant/find-restaurants-name/";
        URL = URL.concat(name);
        return URL;
    }

    private JSONArray jsonObjectTypesOfCuisine(List<String> typesOfCuisine) {
        JSONArray jsonObjectInsertAccomodationReview = new JSONArray();
        return createJsonObjectTypesOfCuisine(jsonObjectInsertAccomodationReview, typesOfCuisine);
    }

    private JSONArray createJsonObjectTypesOfCuisine(@NotNull JSONArray jsonObjectTypesOfCuisine,
                                                     @NotNull List<String> typesOfCuisine) {
        for (String typeOfCuisine : typesOfCuisine)
            jsonObjectTypesOfCuisine.put(typeOfCuisine);
        return jsonObjectTypesOfCuisine;
    }

    @NotNull
    private List<Restaurant> getArrayFromResponse(@NotNull JSONObject response) {
        List<Restaurant> restaurants = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        try {
            jsonArray = response.getJSONArray("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                restaurants.add(gson.fromJson(jsonArray.getString(i), Restaurant.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restaurants;
    }

    @NotNull
    private List<String> getArrayFromResponseRestaurantsName(@NotNull JSONArray response) {
        List<String> restaurantsName = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                restaurantsName.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restaurantsName;
    }

    private Restaurant getRestaurantFromResponse(@NotNull JSONObject response) {
        return new Gson().fromJson(response.toString(), Restaurant.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isStatusCodeOk(int statusCode){
        return statusCode == 200;
    }

}
