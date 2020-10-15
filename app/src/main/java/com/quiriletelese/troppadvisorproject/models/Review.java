package com.quiriletelese.troppadvisorproject.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Review implements Serializable {
    private String id;
    private String title;
    private String description;
    private Double rating;
    private String user;
    private Boolean isAnonymous;
    private String accomodationId;
    private String addedDate;
    private String lastModificationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getAccomodationId() {
        return accomodationId;
    }

    public void setAccomodationId(String accomodationId) {
        this.accomodationId = accomodationId;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

}
