package com.quiriletelese.troppadvisorproject.models;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Restaurant extends Accomodation {
    private List<String> typeOfCuisine;

    public List<String> getTypeOfCuisine() {
        return typeOfCuisine;
    }

    public void setTypeOfCuisine(List<String> typeOfCuisine) {
        this.typeOfCuisine = typeOfCuisine;
    }
}