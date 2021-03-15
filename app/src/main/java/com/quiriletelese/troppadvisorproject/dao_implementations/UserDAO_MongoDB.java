package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.quiriletelese.troppadvisorproject.dao_interfaces.UserDAO;
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Attraction;
import com.quiriletelese.troppadvisorproject.models.User;
import com.quiriletelese.troppadvisorproject.utils.MultipartRequest;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO_MongoDB implements UserDAO {
    @Override
    public void findByEmail(VolleyCallBack volleyCallBack, String email, Context context) {
        findByEmailVolley(volleyCallBack, email, context);
    }

    @Override
    public void findByNameOrUsername(VolleyCallBack volleyCallBack, String value, Context context) {
        findByNameOrUsernameVolley(volleyCallBack, value, context);
    }

    @Override
    public void findLeaderboard(VolleyCallBack volleyCallBack, Context context) {
        findLeaderboardVolley(volleyCallBack, context);
    }

    @Override
    public void updateUserImage(VolleyCallBack volleyCallBack, String email, byte[] file, Context context) {
        updateUserImageVolley(volleyCallBack, email, file, context);
    }

    @Override
    public void updateUserInformations(VolleyCallBack volleyCallBack, User user, Context context) {
        updateUserInformationsVolley(volleyCallBack, user, context);
    }

    private void findByEmailVolley(VolleyCallBack volleyCallBack, String email, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByEmailUrl(email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    volleyCallBack.onSuccess(getUserFromResponse(response));
                },
                error -> {

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

    private void findByNameOrUsernameVolley(VolleyCallBack volleyCallBack, String value, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindByNameOrUsernameUrl(value);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    volleyCallBack.onSuccess(getArrayFromResponse(response));
                },
                error -> {

                }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void findLeaderboardVolley(VolleyCallBack volleyCallBack, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createFindLeaderboardUrl();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    volleyCallBack.onSuccess(getArrayFromResponse(response));
                },
                error -> {

                }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private void updateUserImageVolley(VolleyCallBack volleyCallBack, String email, byte[] file, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = cretateUpdateUserImageUrl(email);
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.PUT, URL,
                response -> {
                    volleyCallBack.onSuccess(getUpdateUserImageResultFromResponse(response));
                },
                error -> {

                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", file));
                return params;
            }

            @Override
            protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(multipartRequest);
    }

    private void updateUserInformationsVolley(VolleyCallBack volleyCallBack, User user, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createUpdateUserInfromationsUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectUpdateUserInformations(user),
                response -> {
                    volleyCallBack.onSuccess(getUserFromResponse(response));
                },
                error -> {

                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    checkCreateAccountVolleyError(response, volleyCallBack);
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.start();
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject jsonObjectUpdateUserInformations(User user) {
        JSONObject jsonObjectInsertAccomodationReview = new JSONObject();
        return createJsonObjectInsertAccomodationReview(jsonObjectInsertAccomodationReview, user);
    }

    private JSONObject createJsonObjectInsertAccomodationReview(@NotNull JSONObject jsonObjectInsertAccomodationReview, @NotNull User user) {
        try {
            jsonObjectInsertAccomodationReview.put("name", user.getName());
            jsonObjectInsertAccomodationReview.put("lastName", user.getLastName());
            jsonObjectInsertAccomodationReview.put("username", user.getUsername());
            jsonObjectInsertAccomodationReview.put("chosenTitle", user.getChosenTitle());
            jsonObjectInsertAccomodationReview.put("isPrivateAccount", user.isPrivateAccount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectInsertAccomodationReview;
    }

    private String createFindByEmailUrl(String email) {
        return Constants.getBaseUrl() + Constants.getUserRoute() + Constants.getFindUserByEmailRoute() + email;
    }

    private String createFindByNameOrUsernameUrl(String value) {
        return Constants.getBaseUrl() + Constants.getUserRoute() + Constants.getFindUsersByNameOrUsermaneRoute() + value;
    }

    private String createFindLeaderboardUrl() {
        return Constants.getBaseUrl() + Constants.getUserRoute() + Constants.getFindLeaderboardUser();
    }

    private String cretateUpdateUserImageUrl(String email) {
        return Constants.getBaseUrl() + Constants.getUserRoute() + Constants.getUpdateUserImageRoute() + email;
    }

    private String createUpdateUserInfromationsUrl() {
        return Constants.getBaseUrl() + Constants.getUserRoute() + Constants.getUpdateUserInformationsRoute();
    }

    private User getUserFromResponse(@NonNull JSONObject response) {
        return new Gson().fromJson(response.toString(), User.class);
    }

    @NotNull
    private List<User> getArrayFromResponse(@NotNull JSONArray response) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                users.add(getUserFromResponse(response.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    private Boolean getUpdateUserImageResultFromResponse(@NonNull NetworkResponse response) {
        return new Gson().fromJson(response.toString(), Boolean.class);
    }

    private void checkCreateAccountVolleyError(@NotNull NetworkResponse networkResponse, VolleyCallBack volleyCallBack) {
        if (networkResponse.headers.containsKey(Constants.getUsernameError()))
            volleyCallBack.onError(Constants.getUsernameError());
        else
            volleyCallBack.onError(String.valueOf(networkResponse.statusCode));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isStatusCodeOk(int statusCode) {
        return statusCode == 200;
    }

}
