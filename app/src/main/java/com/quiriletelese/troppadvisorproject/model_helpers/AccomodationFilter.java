package com.quiriletelese.troppadvisorproject.model_helpers;

import java.io.Serializable;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public abstract class AccomodationFilter implements Serializable {

    private String name;
    private String city;
    private Integer avaragePrice;
    private Integer avarageRating;
    private Double distance;
    private boolean hasCertificateOfExcellence;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getAvaragePrice() {
        return avaragePrice;
    }

    public void setAvaragePrice(Integer avaragePrice) {
        this.avaragePrice = avaragePrice;
    }

    public Integer getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(Integer avarageRating) {
        this.avarageRating = avarageRating;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public boolean isHasCertificateOfExcellence() {
        return hasCertificateOfExcellence;
    }

    public void setHasCertificateOfExcellence(boolean hasCertificateOfExcellence) {
        this.hasCertificateOfExcellence = hasCertificateOfExcellence;
    }

}
