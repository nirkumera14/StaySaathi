package com.pgfinder.platform.dto;

import java.util.List;

public record GooglePlaceDataDto(
    String placeId,
    String mapsUrl,
    Double rating,
    Integer userRatingsTotal,
    List<String> photoUrls,
    List<ReviewDto> reviews,
    List<String> htmlAttributions
) {
}
