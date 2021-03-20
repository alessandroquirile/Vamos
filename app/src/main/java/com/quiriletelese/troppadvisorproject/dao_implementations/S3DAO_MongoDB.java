package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.S3DAO;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.MultipartRequest;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class S3DAO_MongoDB implements S3DAO {

    @Override
    public void uploadFile(VolleyCallBack volleyCallBack, byte[] file, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createUploadFileUrl();
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, URL,
                response -> {
                    volleyCallBack.onSuccess(getImageUrlFromResponse(response));
                },
                error -> {
                    volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
                }) {
            @Override
            protected Map<String, MultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", file));
                return params;
            }
        };
        requestQueue.start();
        requestQueue.add(multipartRequest);
    }

    @Override
    public void deleteFile(VolleyCallBack volleyCallBack, String image, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createDeleteFileUrl(image);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, URL, null,
                response -> {

                },
                error -> {

        });
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    @NotNull
    @Contract(pure = true)
    private String createUploadFileUrl() {
        return Constants.getBaseUrl() + Constants.getS3Route() + Constants.getS3UploadFileRoute();
    }

    @NotNull
    @Contract(pure = true)
    private String createDeleteFileUrl(String image) {
        return Constants.getBaseUrl() + Constants.getS3Route() + Constants.getS3DeleteFileRoute() + "?" + image;
    }

    private String getImageUrlFromResponse(@NotNull NetworkResponse response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), String.class);
    }

}
