package com.quiriletelese.troppadvisorproject.models;

import java.io.Serializable;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ChangeUserPassword implements Serializable {

    private String accessToken;
    private String previousPassword;
    private String proposedPassword;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPreviousPassword() {
        return previousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public String getProposedPassword() {
        return proposedPassword;
    }

    public void setProposedPassword(String proposedPassword) {
        this.proposedPassword = proposedPassword;
    }

}