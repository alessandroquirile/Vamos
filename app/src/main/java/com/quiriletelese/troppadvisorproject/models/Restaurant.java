package com.quiriletelese.troppadvisorproject.models;

import android.graphics.Point;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private List<String> typeOfCuisine;
    private Integer avarageRating;
    private Double avaragePrice;
    private String phoneNumber;
    private Address address;
    private Point point;
    private List<Review> reviews;
    private List<String> images;
    private boolean certificateOfExcellence;
    private Date addedDate;
    private Date lastModificationDate;

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

    public List<String> getTypeOfCuisine() {
        return typeOfCuisine;
    }

    public void setTypeOfCuisine(List<String> typeOfCuisine) {
        this.typeOfCuisine = typeOfCuisine;
    }

    public Integer getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(Integer avarageRating) {
        this.avarageRating = avarageRating;
    }

    public Double getAvaragePrice() {
        return avaragePrice;
    }

    public void setAvaragePrice(Double avaragePrice) {
        this.avaragePrice = avaragePrice;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isCertificateOfExcellence() {
        return certificateOfExcellence;
    }

    public void setCertificateOfExcellence(boolean certificateOfExcellence) {
        this.certificateOfExcellence = certificateOfExcellence;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

}
