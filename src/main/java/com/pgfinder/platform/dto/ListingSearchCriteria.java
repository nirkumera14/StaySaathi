package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.GenderType;
import com.pgfinder.platform.domain.RoomType;
import com.pgfinder.platform.domain.SortBy;

import java.util.HashSet;
import java.util.Set;

public class ListingSearchCriteria {

    private String q;
    private String city;
    private GenderType genderType;
    private RoomType roomType;
    private Integer minRent;
    private Integer maxRent;
    private Boolean foodIncluded;
    private Boolean verified;
    private Set<Amenity> amenities = new HashSet<>();
    private SortBy sortBy = SortBy.RELEVANCE;
    private int page = 0;
    private int size = 18;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Integer getMinRent() {
        return minRent;
    }

    public void setMinRent(Integer minRent) {
        this.minRent = minRent;
    }

    public Integer getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(Integer maxRent) {
        this.maxRent = maxRent;
    }

    public Boolean getFoodIncluded() {
        return foodIncluded;
    }

    public void setFoodIncluded(Boolean foodIncluded) {
        this.foodIncluded = foodIncluded;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<Amenity> amenities) {
        this.amenities = amenities;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}