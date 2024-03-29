package com.quiriletelese.troppadvisorproject.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Review implements Serializable {
    private String id;
    private String title;
    private String description;
    private Double rating;
    private User user;
    private Set<String> voters;
    private Long totalThumbUp;
    private Long totalThumbDown;
    private String accomodationId;
    private String accomodationName;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<String> getVoters() {
        return voters;
    }

    public void setVoters(Set<String> voters) {
        this.voters = voters;
    }

    public Long getTotalThumbUp() {
        return totalThumbUp;
    }

    public void setTotalThumbUp(Long totalThumbUp) {
        this.totalThumbUp = totalThumbUp;
    }

    public Long getTotalThumbDown() {
        return totalThumbDown;
    }

    public void setTotalThumbDown(Long totalThumbDown) {
        this.totalThumbDown = totalThumbDown;
    }

    public String getAccomodationId() {
        return accomodationId;
    }

    public void setAccomodationId(String accomodationId) {
        this.accomodationId = accomodationId;
    }

    public String getAccomodationName() {
        return accomodationName;
    }

    public void setAccomodationName(String accomodationName) {
        this.accomodationName = accomodationName;
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

    public boolean userHasImage() {
        return !user.getImage().isEmpty();
    }

    public String getUserImage() {
        return user.getImage();
    }

}
