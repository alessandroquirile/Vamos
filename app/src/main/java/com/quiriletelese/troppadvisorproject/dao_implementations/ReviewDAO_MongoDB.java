package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ReviewDAO_MongoDB implements ReviewDAO {

    @Override
    public void insertHotelReview(VolleyCallBack volleyCallBack, Review review, String idToken, Context context) {
        insertHotelReviewVolley(volleyCallBack, review, idToken, context);
    }

    @Override
    public void insertRestaurantReview(VolleyCallBack volleyCallBack, Review review, String idToken, Context context) {
        insertRestaurantReviewVolley(volleyCallBack, review, idToken, context);
    }

    @Override
    public void insertAttractionReview(VolleyCallBack volleyCallBack, Review review, String email, Context context) {
        insertAttractionReviewVolley(volleyCallBack, review, email, context);
    }

    @Override
    public void findAccomodationReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        findAccomodationReviewsVolley(volleyCallBack, id, context, page, size);
    }

    @Override
    public void findUserReviews(VolleyCallBack volleyCallBack, String userId, Context context, int page, int size) {
        findUserReviewsVolley(volleyCallBack, userId, context, page, size);
    }

    @Override
    public void updateVoters(VolleyCallBack volleyCallBack, String id, String email, int vote, Context context) {
        updateVotersVolley(volleyCallBack, id, email, vote, context);
    }

    private void insertHotelReviewVolley(VolleyCallBack volleyCallBack, Review review, String idToken, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertHotelReviewUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review),
                response -> {
                    volleyCallBack.onSuccess(getReviewFromResponse(response));
                },
                error -> {
                    if (error != null)
                        volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + idToken);
                return headers;
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void insertRestaurantReviewVolley(VolleyCallBack volleyCallBack, Review review, String idToken, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertRestaurantReviewUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review),
                response -> {
                    volleyCallBack.onSuccess(getReviewFromResponse(response));
                },
                error -> {
                    if (error != null)
                        volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + idToken);
                return headers;
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void insertAttractionReviewVolley(VolleyCallBack volleyCallBack, Review review, String email, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertAttractionReviewUrl(email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review),
                response -> {
                    volleyCallBack.onSuccess(getReviewFromResponse(response));
                },
                error -> {
                    if (error != null)
                        volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
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

    private void findAccomodationReviewsVolley(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindAccomodationReviewsUrl(id, page, size);
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

    private void findUserReviewsVolley(VolleyCallBack volleyCallBack, String userId, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindUserReviewsUrl(userId, page, size);
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

    private void updateVotersVolley(VolleyCallBack volleyCallBack, String id, String email, int vote, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createUpdateVotersUrl(id, email, vote);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, null,
                response -> {
                    volleyCallBack.onSuccess(getBooleanFromResponse(response));
                },
                error -> {

                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    checkUpdateVotersVolleyError(response, volleyCallBack);
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    @NotNull
    @Contract(pure = true)
    private String createInsertHotelReviewUrl() {
        return Constants.getBaseUrl() + "review/insert-hotel-review";
    }

    @NotNull
    @Contract(pure = true)
    private String createInsertRestaurantReviewUrl() {
        return Constants.getBaseUrl() + "review/insert-restaurant-review";
    }

    @NotNull
    @Contract(pure = true)
    private String createInsertAttractionReviewUrl(String email) {
        return Constants.getBaseUrl() + "review/insert-attraction-review/" + email;
    }

    @NotNull
    private String createFindAccomodationReviewsUrl(String id, int page, int size) {
        String URL = Constants.getBaseUrl() + "review/find-accomodation-reviews?";
        URL = URL.concat("id=" + id);
        URL = URL.concat("&page=" + page);
        URL = URL.concat("&size=" + size);
        return URL;
    }

    @NotNull
    private String createFindUserReviewsUrl(String id, int page, int size) {
        String URL = Constants.getBaseUrl() + "review/find-user-reviews?";
        URL = URL.concat("userId=" + id);
        URL = URL.concat("&page=" + page);
        URL = URL.concat("&size=" + size);
        return URL;
    }

    @NotNull
    private String createUpdateVotersUrl(String id, String email, int vote) {
        String URL = Constants.getBaseUrl() + "review/update-voters?";
        URL = URL.concat("id=" + id);
        URL = URL.concat("&email=" + email);
        URL = URL.concat("&vote=" + vote);
        return URL;
    }

    private JSONObject jsonObjectInsertAccomodationReview(Review review) {
        JSONObject jsonObjectInsertAccomodationReview = new JSONObject();
        return createJsonObjectInsertAccomodationReview(jsonObjectInsertAccomodationReview, review);
    }

    private JSONObject createJsonObjectInsertAccomodationReview(@NotNull JSONObject jsonObjectInsertAccomodationReview, @NotNull Review review) {
        try {
            jsonObjectInsertAccomodationReview.put("title", review.getTitle());
            jsonObjectInsertAccomodationReview.put("description", review.getDescription());
            jsonObjectInsertAccomodationReview.put("rating", review.getRating());
            jsonObjectInsertAccomodationReview.put("user", review.getUser());
            jsonObjectInsertAccomodationReview.put("accomodationId", review.getAccomodationId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectInsertAccomodationReview;
    }

    private Review getReviewFromResponse(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), Review.class);
    }

    private Boolean getBooleanFromResponse(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), Boolean.class);
    }

    @NotNull
    private List<Review> getArrayFromResponse(@NotNull JSONObject response) {
        List<Review> reviews = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        try {
            jsonArray = response.getJSONArray("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                reviews.add(gson.fromJson(jsonArray.getString(i), Review.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return reviews;
    }

    private void checkUpdateVotersVolleyError(@NotNull NetworkResponse networkResponse, VolleyCallBack volleyCallBack) {
        if (networkResponse.headers.containsKey(Constants.getAlreadyVotedError()))
            volleyCallBack.onError(Constants.getAlreadyVotedError());
        else
            volleyCallBack.onError(String.valueOf(networkResponse.statusCode));
    }

    private boolean isStatusCodeOk(int statusCode) {
        return statusCode == 200;
    }

}
