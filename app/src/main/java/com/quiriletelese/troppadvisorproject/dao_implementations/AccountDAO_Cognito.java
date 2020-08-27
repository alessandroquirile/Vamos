package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.amazonaws.services.cognitoidentityprovider.model.GetUserResult;
import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.quiriletelese.troppadvisorproject.dao_interfaces.AccountDAO;
import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallbackCreateUser;
import com.quiriletelese.troppadvisorproject.interfaces.VolleyCallbackLogin;
import com.quiriletelese.troppadvisorproject.models.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AccountDAO_Cognito implements AccountDAO {

    @Override
    public void login(VolleyCallbackLogin volleyCallbackLogin, Account account, Context context) {
        loginVolley(volleyCallbackLogin, account, context);
    }

    @Override
    public void createAccount(VolleyCallbackCreateUser volleyCallbackCreateUser, Account account, Context context) {
        createUserVolley(volleyCallbackCreateUser, account, context);
    }

    @Override
    public void updatePassword(Account account, Context context, String newPassword) {

    }

    private void loginVolley(final VolleyCallbackLogin volleyCallbackLogin, Account account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createLoginUrl(account);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallbackLogin.onSuccess(getInitiateAuthResultFromVolley(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void createUserVolley(final VolleyCallbackCreateUser volleyCallbackCreateUser, Account account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = createNewUserURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectNewUser(account), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallbackCreateUser.onSuccess(getUserResultFromVolley(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private String createLoginUrl(Account account) {
        String URL = "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/cognito/login?";
        URL = URL.concat("username=" + account.getUsername());
        URL = URL.concat("&password=" + account.getPassword());
        return URL;
    }

    private JSONObject jsonObjectNewUser(Account account) {
        return createJsonObjectNewUser(account);
    }

    private JSONObject createJsonObjectNewUser(Account account) {
        JSONObject jsonObjectNewUSer = new JSONObject();
        try {
            jsonObjectNewUSer.put("name", account.getName());
            jsonObjectNewUSer.put("lastname", account.getLastname());
            jsonObjectNewUSer.put("username", account.getUsername());
            jsonObjectNewUSer.put("email", account.getEmail());
            jsonObjectNewUSer.put("password", account.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectNewUSer;
    }

    private String createNewUserURL() {
        return "http://Troppadvisorserver-env.eba-pfsmp3kx.us-east-1.elasticbeanstalk.com/cognito/insert-user";
    }

    private InitiateAuthResult getInitiateAuthResultFromVolley(JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), InitiateAuthResult.class);
    }

    private GetUserResult getUserResultFromVolley(JSONObject response){
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), GetUserResult.class);
    }
}
