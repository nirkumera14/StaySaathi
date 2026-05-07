package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.GenderType;

import java.util.List;

public record ListingSummaryDto(
    Long id,
    String slug,
    String title,
    String locality,
    String city,
    GenderType genderType,
    Integer startingPrice,
    Integer endingPrice,
    boolean foodIncluded,
    boolean verified,
    boolean partnerVerified,
    boolean brandNew,
    Double ratingAvg,
    Integer reviewCount,
    Integer availableBeds,
    String mainImageUrl,
    String shortDescription,
    List<String> topAmenities,
    String nearbyMetro,
    String nearbyLandmark
) {
}