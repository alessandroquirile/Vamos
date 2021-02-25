package com.quiriletelese.troppadvisorproject.models;

import com.quiriletelese.troppadvisorproject.model_helpers.Badge;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {

    private String id;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String image;
    private Long totalReviews;
    private Double avarageRating;
    private Double totalRating;
    private Double level;
    private Set<Badge> obtainedBadges;
    private Set<Badge> missingBadges;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Double getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(Double avarageRating) {
        this.avarageRating = avarageRating;
    }

    public Double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Double totalRating) {
        this.totalRating = totalRating;
    }

    public Double getLevel() {
        return level;
    }

    public void setLevel(Double level) {
        this.level = level;
    }

    public Set<Badge> getObtainedBadges() {
        return obtainedBadges;
    }

    public void setObtainedBadges(Set<Badge> obtainedBadges) {
        this.obtainedBadges = obtainedBadges;
    }

    public Set<Badge> getMissingBadges() {
        return missingBadges;
    }

    public void setMissingBadges(Set<Badge> missingBadges) {
        this.missingBadges = missingBadges;
    }
}
