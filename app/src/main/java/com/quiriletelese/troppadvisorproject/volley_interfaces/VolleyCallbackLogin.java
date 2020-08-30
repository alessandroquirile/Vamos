package com.quiriletelese.troppadvisorproject.volley_interfaces;

import com.amazonaws.services.cognitoidentityprovider.model.InitiateAuthResult;

public interface VolleyCallbackLogin {

    void onSuccess(InitiateAuthResult initiateAuthResult);

    void onError(String error);

}
