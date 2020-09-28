package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TypeOfCuisineDAO_MongoDB implements TypeOfCuisineDAO {

    List<String> typeOfCuisine = new ArrayList<>();

    @Override
    public void getAll(VolleyCallBack volleyCallBack, Context context) {
        getAllVolley(volleyCallBack, context);
    }

    private void getAllVolley(final VolleyCallBack volleyCallBack, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createGetAllUrl();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
            getArrayFromResponse(response);
            volleyCallBack.onSuccess(typeOfCuisine);
        }, error -> {

        });
        requestQueue.add(jsonArrayRequest);
    }

    private String createGetAllUrl(){
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/type-of-cuisine/get-all";
    }

    private void getArrayFromResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                typeOfCuisine.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
