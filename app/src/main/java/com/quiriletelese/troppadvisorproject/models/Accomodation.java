package com.quiriletelese.troppadvisorproject.models;


import java.io.Serializable;
import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public abstract class Accomodation implements Serializable {

    protected String id;
    protected String name;
    protected Double avarageRating;
    protected String phoneNumber;
    protected Address address;
    protected Point point;
    protected List<Review> reviews;
    private Long totalReviews;
    private Double totalRating;
    protected List<String> images;
    private String webSite;
    protected boolean certificateOfExcellence;
    protected String addedDate;
    protected String lastModificationDate;

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

    public Double getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(Double avarageRating) {
        this.avarageRating = avarageRating;
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

    public Double getLatitude() {
        return this.point.getX();
    }

    public Double getLongitude() {
        return this.point.getY();
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

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Double totalRating) {
        this.totalRating = totalRating;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public boolean isCertificateOfExcellence() {
        return certificateOfExcellence;
    }

    public void setCertificateOfExcellence(boolean certificateOfExcellence) {
        this.certificateOfExcellence = certificateOfExcellence;
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

    public String getCity() {
        return this.address.getCity();
    }

    public String getStreet() {
        return this.address.getStreet();
    }

    public String getHouseNumber() {
        return this.address.getHouseNumber();
    }

    public String getPostalCode() {
        return this.address.getPostalCode();
    }

    public String getProvince() {
        return this.address.getProvince();
    }

    public String getTypeOfAddress() {
        return this.address.getType();
    }

    public boolean hasImage() {
        return this.images.size() > 0;
    }

    public boolean hasAvarageRating() {
        return !this.avarageRating.equals(0d);
    }

    public boolean hasReviews() {
        return this.totalReviews > 0;
    }

    public String getFirstImage() {
        return this.images.get(0);
    }

}
