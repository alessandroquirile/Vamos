package com.quiriletelese.troppadvisorproject.dao_implementations;

import android.content.Context;

import com.amazonaws.services.cognitoidentityprovider.model.CodeDeliveryDetailsType;
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
import com.quiriletelese.troppadvisorproject.model_helpers.Constants;
import com.quiriletelese.troppadvisorproject.models.Account;
import com.quiriletelese.troppadvisorproject.volley_interfaces.VolleyCallBack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class AccountDAO_Cognito implements AccountDAO {

    @Override
    public void login(VolleyCallBack volleyCallBack, Account account, Context context) {
        loginVolley(volleyCallBack, account, context);
    }

    @Override
    public void createAccount(VolleyCallBack volleyCallBack, Account account, Context context) {
        createAccountVolley(volleyCallBack, account, context);
    }

    @Override
    public void refreshToken(VolleyCallBack volleyCallBack, String refreshToken, Context context) {
        refreshTokenVolley(volleyCallBack, refreshToken, context);
    }

    @Override
    public void getUserDetails(VolleyCallBack volleyCallBack, String accessToken, Context context) {
        getUserDetailsVolley(volleyCallBack, accessToken, context);
    }

    @Override
    public void sendConfirmationCode(VolleyCallBack volleyCallBack, String email, Context context) {
        sendConfirmationCodeVolley(volleyCallBack, email, context);
    }

    @Override
    public void changePassword(VolleyCallBack volleyCallBack, String email, String confirmationCode, String newPassword, Context context) {
        changePasswordVolley(volleyCallBack, email, confirmationCode, newPassword, context);
    }

    private void loginVolley(final VolleyCallBack volleyCallBack, Account account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createLoginUrl();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                jsonObjectLogin(account), response -> {
            volleyCallBack.onSuccess(getInitiateAuthResultFromVolley(response));
        }, error -> {

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse
                                                                        response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void createAccountVolley(final VolleyCallBack volleyCallBack, Account
            account, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createNewUserURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                jsonObjectNewUser(account), response ->
                volleyCallBack.onSuccess(getUserResultFromVolley(response)),
                error -> {
                    if (error != null)
                        checkCreateAccountVolleyError(error.networkResponse, volleyCallBack);
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                checkCreateAccountVolleyError(response, volleyCallBack);
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void refreshTokenVolley(VolleyCallBack volleyCallBack, String refreshToken, Context
            context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createRefreshTokenURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                jsonObjectRefreshToken(refreshToken), response ->
                volleyCallBack.onSuccess(getInitiateAuthResultFromVolley(response)), error -> {
            if (error != null)
                volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void getUserDetailsVolley(VolleyCallBack volleyCallBack, String
            accessToken, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createGetUserDetailsURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObjectGetUserDetails(accessToken),
                response -> volleyCallBack.onSuccess(getUserResultFromVolley(response)), error -> {
            if (error != null)
                volleyCallBack.onError(String.valueOf(error.networkResponse.statusCode));
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void sendConfirmationCodeVolley(VolleyCallBack volleyCallBack, String
            email, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createSendConfirmationCodeURL(email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> volleyCallBack.onSuccess(getCodeDeliveryDetailsTypeFromVolley(response)),
                error -> {
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void changePasswordVolley(VolleyCallBack volleyCallBack, String email, String
            confirmationCode, String newPassword, Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        String URL = createChangePasswordURL(email, newPassword, confirmationCode);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> volleyCallBack.onSuccess(getUserResultFromVolley(response)),
                error -> {
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NotNull NetworkResponse response) {
                if (!isStatusCodeOk(response.statusCode))
                    volleyCallBack.onError(String.valueOf(response.statusCode));
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @NotNull
    @Contract(pure = true)
    private String createLoginUrl() {
        return Constants.getBaseUrl() + "cognito/login";
    }

    @NotNull
    @Contract(pure = true)
    private String createNewUserURL() {
        return Constants.getBaseUrl() + "cognito/insert-user";
    }

    @NotNull
    @Contract(pure = true)
    private String createRefreshTokenURL() {
        return Constants.getBaseUrl() + "cognito/refresh-token";
    }

    @NotNull
    @Contract(pure = true)
    private String createGetUserDetailsURL() {
        return Constants.getBaseUrl() + "cognito/get-user-details";
    }

    @NotNull
    @Contract(pure = true)
    private String createSendConfirmationCodeURL(String email) {
        return Constants.getBaseUrl() + Constants.getCognitoRoute() + Constants.getSendConfirmationCodeRoute() + email;
    }

    @NotNull
    @Contract(pure = true)
    private String createChangePasswordURL(String email, String password, String code) {
        return Constants.getBaseUrl() + Constants.getCognitoRoute() + Constants.getChangePassworRoute()
                + "email=" + email + "&password=" + password + "&code=" + code;
    }

    private JSONObject jsonObjectNewUser(Account account) {
        JSONObject jsonObjectNewUSer = new JSONObject();
        return createJsonObjectNewUser(jsonObjectNewUSer, account);
    }

    private JSONObject jsonObjectLogin(Account account) {
        JSONObject jsonObjectLogin = new JSONObject();
        return createJsonObjectLogin(jsonObjectLogin, account);
    }

    private JSONObject jsonObjectRefreshToken(String refreshToken) {
        JSONObject jsonObjectRefreshToken = new JSONObject();
        return createJsonObjectRefreshToken(jsonObjectRefreshToken, refreshToken);
    }

    private JSONObject jsonObjectGetUserDetails(String accessToken) {
        JSONObject jsonObjectGetUserDetails = new JSONObject();
        return createJsonObjectGetUserDetails(jsonObjectGetUserDetails, accessToken);
    }

    private JSONObject createJsonObjectNewUser(@NotNull JSONObject
                                                       jsonObjectNewUser, @NotNull Account account) {
        try {
            jsonObjectNewUser.put("name", account.getName());
            jsonObjectNewUser.put("lastname", account.getFamilyName());
            jsonObjectNewUser.put("username", account.getUsername());
            jsonObjectNewUser.put("email", account.getEmail());
            jsonObjectNewUser.put("password", account.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectNewUser;
    }

    private JSONObject createJsonObjectLogin(@NotNull JSONObject
                                                     jsonObjectLogin, @NotNull Account account) {
        try {

            jsonObjectLogin.put("key", account.getUsername());
            jsonObjectLogin.put("password", account.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectLogin;
    }

    private JSONObject createJsonObjectRefreshToken(@NotNull JSONObject
                                                            jsonObjectRefreshToken, @NotNull String refreshToken) {
        try {
            jsonObjectRefreshToken.put("refreshToken", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectRefreshToken;
    }

    private JSONObject createJsonObjectGetUserDetails(@NotNull JSONObject
                                                              jsonObjectGetUserDetails, @NotNull String accessToken) {
        try {
            jsonObjectGetUserDetails.put("accessToken", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectGetUserDetails;
    }

    private InitiateAuthResult getInitiateAuthResultFromVolley(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), InitiateAuthResult.class);
    }

    private GetUserResult getUserResultFromVolley(@NotNull JSONObject response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), GetUserResult.class);
    }

    private CodeDeliveryDetailsType getCodeDeliveryDetailsTypeFromVolley(@NotNull JSONObject
                                                                                 response) {
        Gson gson = new Gson();
        return gson.fromJson(response.toString(), CodeDeliveryDetailsType.class);
    }

    private void checkCreateAccountVolleyError(@NotNull NetworkResponse
                                                       networkResponse, VolleyCallBack volleyCallBack) {
        if (networkResponse.headers.containsKey(Constants.getUsernameError()))
            volleyCallBack.onError(Constants.getUsernameError());
        else if (networkResponse.headers.containsKey(Constants.getEmailError()))
            volleyCallBack.onError(Constants.getEmailError());
        else
            volleyCallBack.onError(String.valueOf(networkResponse.statusCode));
    }

    private boolean isStatusCodeOk(int statusCode) {
        return statusCode == 200;
    }

}
