package com.quiriletelese.troppadvisorproject.models;

import android.graphics.Point;

import com.quiriletelese.troppadvisorproject.model_helpers.Address;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public abstract class Accomodation implements Serializable {
    protected String id;
    protected String name;
    protected Integer avarageRating;
    protected Double avaragePrice;
    protected String phoneNumber;
    protected Address address;
    protected Point point;
    protected List<Review> reviews;
    protected List<String> images;
    protected boolean hasCertificateOfExcellence;
    protected Date addedDate;
    protected Date lastModificationDate;

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

    public boolean isHasCertificateOfExcellence() {
        return hasCertificateOfExcellence;
    }

    public void setHasCertificateOfExcellence(boolean hasCertificateOfExcellence) {
        this.hasCertificateOfExcellence = hasCertificateOfExcellence;
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
