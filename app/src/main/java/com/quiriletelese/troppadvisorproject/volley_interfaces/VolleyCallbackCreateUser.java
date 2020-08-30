package com.quiriletelese.troppadvisorproject.volley_interfaces;

import com.amazonaws.services.cognitoidentityprovider.model.GetUserResult;

public interface VolleyCallbackCreateUser {

    void onSuccess(GetUserResult getUserResult);

    void onError(String error);

}
