package com.quiriletelese.troppadvisorproject.model_helpers;

import java.util.List;

public class AccomodationRestaurantFilter extends AccomodationFilter{

    private List<String> typesOfCuisine;

    public List<String> getTypesOfCuisine() {
        return typesOfCuisine;
    }

    public void setTypesOfCuisine(List<String> typesOfCuisine) {
        this.typesOfCuisine = typesOfCuisine;
    }

}
