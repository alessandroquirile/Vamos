package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.models.Review;
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
public class ReviewDAO_MongoDB implements ReviewDAO {

    private List<Review> reviews = new ArrayList<>();
    private Review review;

    @Override
    public void insertHotelReview(VolleyCallBack volleyCallBack, Review review, Context context) {
        insertHotelReviewVolley(volleyCallBack, review, context);
    }

    @Override
    public void insertRestaurantReview(VolleyCallBack volleyCallBack, Review review, Context context) {
        insertRestaurantReviewVolley(volleyCallBack, review, context);
    }

    @Override
    public void insertAttractionReview(VolleyCallBack volleyCallBack, Review review, Context context) {
        insertAttractionReviewVolley(volleyCallBack, review, context);
    }

    @Override
    public void findAccomodationReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        findAccomodationReviewsVolley(volleyCallBack, id, context, page, size);
    }

    private void insertHotelReviewVolley(VolleyCallBack volleyCallBack, Review review, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertHotelReviewUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review), response -> {
            this.review = getReviewFromResponse(response);
            System.out.println(this.review.toString());
            volleyCallBack.onSuccess(review);
        }, error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void insertRestaurantReviewVolley(VolleyCallBack volleyCallBack, Review review, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertRestaurantReviewUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review), response -> {
            this.review = getReviewFromResponse(response);
            volleyCallBack.onSuccess(review);
        }, error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void insertAttractionReviewVolley(VolleyCallBack volleyCallBack, Review review, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createInsertAttractionReviewUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectInsertAccomodationReview(review), response -> {
            this.review = getReviewFromResponse(response);
            System.out.println(this.review.toString());
            volleyCallBack.onSuccess(review);
        }, error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findAccomodationReviewsVolley(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindAccomodationReviewsUrl(id, page, size);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponse(response);
            volleyCallBack.onSuccess(reviews);
        }, error -> {

        }){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 204)
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private String createInsertHotelReviewUrl() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/review/insert-hotel-review";
    }

    private String createInsertRestaurantReviewUrl() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/review/insert-restaurant-review";
    }

    private String createInsertAttractionReviewUrl() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/review/insert-attraction-review";
    }

    private String createFindAccomodationReviewsUrl(String id, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/review/find-accomodation-reviews?";
        URL = URL.concat("id=" + id);
        URL = URL.concat("&page=" + page);
        URL = URL.concat("&size=" + size);
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
            jsonObjectInsertAccomodationReview.put("isAnonymous", review.getAnonymous());
            jsonObjectInsertAccomodationReview.put("accomodationId", review.getAccomodationId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectInsertAccomodationReview;
    }

    private Review getReviewFromResponse(JSONObject response){
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), Review.class);
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
                reviews.add(gson.fromJson(jsonArray.getString(i), Review.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
