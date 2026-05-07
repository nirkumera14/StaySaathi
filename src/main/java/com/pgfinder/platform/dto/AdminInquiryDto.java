package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.InquiryStatus;

import java.time.Instant;

public record AdminInquiryDto(
    Long id,
    Long listingId,
    String listingTitle,
    String name,
    String phone,
    String email,
    Integer budget,
    Integer expectedStayMonths,
    InquiryStatus status,
    Instant createdAt
) {
}