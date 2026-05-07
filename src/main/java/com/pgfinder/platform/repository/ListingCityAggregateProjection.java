package com.pgfinder.platform.repository;

public interface ListingCityAggregateProjection {
    String getCity();

    Long getListingCount();

    Double getLatitude();

    Double getLongitude();
}
