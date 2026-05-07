package com.pgfinder.platform.dto;

public record IndiaCityDto(
    String code,
    String city,
    String state,
    String label,
    boolean hasListings,
    long listingCount
) {
}
