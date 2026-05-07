package com.pgfinder.platform.dto;

import java.time.Instant;

public record ReviewDto(
    Long id,
    String reviewerName,
    Double overallRating,
    Double locationRating,
    Double staffRating,
    Double foodRating,
    Double cleanlinessRating,
    Double wifiRating,
    String comment,
    Instant createdAt,
    String source
) {
}