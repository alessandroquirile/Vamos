package com.quiriletelese.troppadvisorproject.models;

import java.io.Serializable;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class PointSearch implements Serializable {

    private Double longitude;
    private Double latitude;
    private Double distance;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
