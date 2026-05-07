package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.GenderType;

import java.util.List;

public record ListingDetailDto(
    Long id,
    String slug,
    String title,
    String city,
    String locality,
    String address,
    String shortDescription,
    String description,
    String brandName,
    GenderType genderType,
    Integer startingPrice,
    Integer endingPrice,
    Integer securityDeposit,
    boolean foodIncluded,
    boolean verified,
    boolean partnerVerified,
    boolean brandNew,
    Integer availableBeds,
    Double ratingAvg,
    Integer reviewCount,
    String nearbyMetro,
    String nearbyLandmark,
    Double latitude,
    Double longitude,
    String contactName,
    String contactPhone,
    String mainImageUrl,
    List<String> galleryImages,
    List<Amenity> amenities,
    List<RoomOptionDto> roomOptions,
    List<ReviewDto> reviews,
    GooglePlaceDataDto googlePlaceData
) {
}