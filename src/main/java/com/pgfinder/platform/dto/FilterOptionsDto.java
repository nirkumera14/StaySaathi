package com.pgfinder.platform.dto;

import java.util.List;

public record FilterOptionsDto(
    List<String> genderTypes,
    List<String> roomTypes,
    List<String> sortOptions,
    List<String> amenities
) {
}
