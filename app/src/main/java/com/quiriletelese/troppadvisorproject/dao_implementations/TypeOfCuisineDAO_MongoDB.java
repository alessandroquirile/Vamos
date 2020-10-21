package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.quiriletelese.troppadvisorproject.dao_interfaces.TypeOfCuisineDAO;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */


public class TypeOfCuisineDAO_MongoDB implements TypeOfCuisineDAO {

    @Override
    public void getAll(VolleyCallBack volleyCallBack, Context context) {
        getAllVolley(volleyCallBack, context);
    }

    private void getAllVolley(final VolleyCallBack volleyCallBack, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createGetAllUrl();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> volleyCallBack.onSuccess(getArrayFromResponse(response)), error -> {

        });
        requestQueue.add(jsonArrayRequest);
    }

    @NotNull
    @Contract(pure = true)
    private String createGetAllUrl() {
        return Constants.getBaseUrl() + "type-of-cuisine/get-all";
    }

    @NotNull
    private List<String> getArrayFromResponse(@NotNull JSONArray response) {
        List<String> typesOfCuisine = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                typesOfCuisine.add(response.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return typesOfCuisine;
    }

}
