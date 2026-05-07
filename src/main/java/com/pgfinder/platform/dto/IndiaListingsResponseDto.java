package com.pgfinder.platform.dto;

import java.util.List;

public record IndiaListingsResponseDto(
    List<ListingSummaryDto> items,
    long total,
    int page,
    int size,
    boolean fallbackApplied,
    String fallbackFromCity,
    String fallbackToCity,
    Double fallbackDistanceKm
) {
}
