package com.pgfinder.platform.dto;

public record IndiaCitySummaryDto(
    String city,
    String state,
    long listingCount
) {
}
