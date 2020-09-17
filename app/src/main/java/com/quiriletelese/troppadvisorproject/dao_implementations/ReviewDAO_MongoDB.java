package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.models.Hotel;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class ReviewDAO_MongoDB implements ReviewDAO {

    private List<Review> reviews = new ArrayList<>();

    @Override
    public void insert(VolleyCallBack volleyCallBack, Review review, Context context) {

    }

    @Override
    public void findHotelReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {

    }

    @Override
    public void findRestaurantReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        findRestaurantReviewsVolley(volleyCallBack, id, context, page, size);
    }

    @Override
    public void findAttractionReviews(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {

    }

    private void findRestaurantReviewsVolley(VolleyCallBack volleyCallBack, String id, Context context, int page, int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindRestaurantReviewsUrl(id, page, size);
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
        requestQueue.add(jsonObjectRequest);
    }

    private String createFindRestaurantReviewsUrl(String id, int page, int size) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/review/find-restaurant-reviews?";
        URL = URL.concat("id=" + id);
        URL = URL.concat("&page=" + page);
        URL = URL.concat("&size=" + size);
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
                reviews.add(gson.fromJson(jsonArray.getString(i), Review.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
