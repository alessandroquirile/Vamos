package com.quiriletelese.troppadvisorproject.models;

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
