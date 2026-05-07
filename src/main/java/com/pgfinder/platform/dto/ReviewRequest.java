package com.pgfinder.platform.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReviewRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String reviewerName;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double overallRating;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double locationRating;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double staffRating;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double foodRating;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double cleanlinessRating;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double wifiRating;

    @NotBlank
    @Size(min = 10, max = 500)
    private String comment;

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public Double getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(Double locationRating) {
        this.locationRating = locationRating;
    }

    public Double getStaffRating() {
        return staffRating;
    }

    public void setStaffRating(Double staffRating) {
        this.staffRating = staffRating;
    }

    public Double getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(Double foodRating) {
        this.foodRating = foodRating;
    }

    public Double getCleanlinessRating() {
        return cleanlinessRating;
    }

    public void setCleanlinessRating(Double cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public Double getWifiRating() {
        return wifiRating;
    }

    public void setWifiRating(Double wifiRating) {
        this.wifiRating = wifiRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}