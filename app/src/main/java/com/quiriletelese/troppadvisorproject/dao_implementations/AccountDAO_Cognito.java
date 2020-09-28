package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.amazonaws.services.cognitoidentityprovider.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidentityprovider.model.GetUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.model_helpers.ChangeUserPassword;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AccountDAO_Cognito implements AccountDAO, Constants {

    @Override
    public void login(VolleyCallBack volleyCallBack, Account account, Context context) {
        loginVolley(volleyCallBack, account, context);
    }

    @Override
    public void createAccount(VolleyCallBack volleyCallBack, Account account, Context context) {
        createAccountVolley(volleyCallBack, account, context);
    }

    @Override
    public void updatePassword(VolleyCallBack volleyCallBack, ChangeUserPassword changeUserPassword, Context context) {
        updatePasswordVolley(volleyCallBack, changeUserPassword, context);
    }

    private void loginVolley(final VolleyCallBack volleyCallBack, Account account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createLoginUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectLogin(account), response -> {
            volleyCallBack.onSuccess(getInitiateAuthResultFromVolley(response));

        }, error -> {
            volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void createAccountVolley(final VolleyCallBack volleyCallBack, Account account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createNewUserURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectNewUser(account), response -> {
            volleyCallBack.onSuccess(getUserResultFromVolley(response));
        }, error -> {
            if (error.networkResponse.headers.containsKey(USERNAME_ERROR))
                volleyCallBack.onError(USERNAME_ERROR);
            else if (error.networkResponse.headers.containsKey(EMAIL_ERROR))
                volleyCallBack.onError(EMAIL_ERROR);
            else
                volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void updatePasswordVolley(final VolleyCallBack volleyCallBack, ChangeUserPassword changeUserPassword, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createUpdatePasswordUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, jsonObjectUpdatePassword(changeUserPassword), response -> {
            volleyCallBack.onSuccess(getChangePasswordResultFromVolley(response));
        }, error -> {
            volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private String createLoginUrl() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/cognito/login";
    }

    private String createNewUserURL() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/cognito/insert-user";
    }

    private String createUpdatePasswordUrl() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/cognito/update-password";
    }

    private JSONObject jsonObjectNewUser(Account account) {
        JSONObject jsonObjectNewUSer = new JSONObject();
        return createJsonObjectNewUser(jsonObjectNewUSer, account);
    }

    private JSONObject createJsonObjectNewUser(@NotNull JSONObject jsonObjectNewUser, @NotNull Account account) {
        try {
            jsonObjectNewUser.put("name", account.getName());
            jsonObjectNewUser.put("lastname", account.getLastname());
            jsonObjectNewUser.put("username", account.getUsername());
            jsonObjectNewUser.put("email", account.getEmail());
            jsonObjectNewUser.put("password", account.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectNewUser;
    }

    private JSONObject jsonObjectLogin(Account account) {
        JSONObject jsonObjectNewUSer = new JSONObject();
        return createJsonObjectLogin(jsonObjectNewUSer, account);
    }

    private JSONObject createJsonObjectLogin(@NotNull JSONObject jsonObjectNewUser, @NotNull Account account) {
        try {
            jsonObjectNewUser.put("key", account.getUsername());
            jsonObjectNewUser.put("password", account.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectNewUser;
    }

    private JSONObject jsonObjectUpdatePassword(ChangeUserPassword changeUserPassword) {
        JSONObject jsonObjectUpdatePassword = new JSONObject();
        return createJsonObjectUpdatePassword(jsonObjectUpdatePassword, changeUserPassword);
    }

    private JSONObject createJsonObjectUpdatePassword(@NotNull JSONObject jsonObjectUpdatePassword, @NotNull ChangeUserPassword changeUserPassword) {
        try {
            jsonObjectUpdatePassword.put("accessToken", changeUserPassword.getAccessToken());
            jsonObjectUpdatePassword.put("previousPassword", changeUserPassword.getPreviousPassword());
            jsonObjectUpdatePassword.put("proposedPassword", changeUserPassword.getProposedPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectUpdatePassword;
    }

    private InitiateAuthResult getInitiateAuthResultFromVolley(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), InitiateAuthResult.class);
    }

    private GetUserResult getUserResultFromVolley(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), GetUserResult.class);
    }

    private ChangePasswordResult getChangePasswordResultFromVolley(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), ChangePasswordResult.class);
    }

}
