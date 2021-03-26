package com.quiriletelese.troppadvisorproject.models;

import java.util.Map;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Attraction extends Accomodation {

    private Map<Integer, String> openingDays;
    private Map<String, Double> price;
    private Double maxPrice;
    private Integer freeAccessPrice;


    public Map<Integer, String> getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(Map<Integer, String> openingDays) {
        this.openingDays = openingDays;
    }

    public Map<String, Double> getPrice() {
        return price;
    }

    public void setPrice(Map<String, Double> price) {
        this.price = price;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getFreeAccessPrice() {
        return freeAccessPrice;
    }

    public void setFreeAccessPrice(Integer freeAccessPrice) {
        this.freeAccessPrice = freeAccessPrice;
    }
}