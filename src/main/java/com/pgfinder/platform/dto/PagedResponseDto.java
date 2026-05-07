package com.pgfinder.platform.dto;

import java.util.List;

public record PagedResponseDto<T>(
    List<T> items,
    long total,
    int page,
    int size
) {
}