package com.quiriletelese.troppadvisorproject.interfaces;

import com.amazonaws.services.cognitoidentityprovider.model.ChangePasswordResult;

public interface VolleyCallbackUpdatePassword {

    void onSuccess(ChangePasswordResult changePasswordResult);
    void onError(String error);

}
