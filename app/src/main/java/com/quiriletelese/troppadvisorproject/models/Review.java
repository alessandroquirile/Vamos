package com.quiriletelese.troppadvisorproject.models;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class Review {

    private String title;
    private String description;
    private int rank;
    private boolean isAnonymous;

    public Review(String title, String description, int rank, boolean isAnonymous) {
        this.title = title;
        this.description = description;
        this.rank = rank;
        this.isAnonymous = isAnonymous;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    @Override
    public String toString() {
        return "Review{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", rank=" + rank +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
