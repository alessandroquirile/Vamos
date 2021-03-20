package com.quiriletelese.troppadvisorproject.models;

import java.util.Map;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Attraction extends Accomodation {

    private Map<Integer, String> openingDays;

    public Map<Integer, String> getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(Map<Integer, String> openingDays) {
        this.openingDays = openingDays;
    }
}