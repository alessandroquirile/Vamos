package com.quiriletelese.troppadvisorproject.model_helpers;

import java.io.Serializable;

public class UserCognitoAttributes implements Serializable {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}