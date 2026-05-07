package com.pgfinder.platform.dto;

public record AdminMetricsDto(
    long totalListings,
    long totalInquiries,
    long newInquiries,
    long contactedInquiries,
    long closedInquiries
) {
}