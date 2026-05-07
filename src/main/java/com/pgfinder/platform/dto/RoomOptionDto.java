package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.RoomType;

public record RoomOptionDto(
    RoomType roomType,
    String label,
    Integer price,
    boolean acIncluded,
    boolean attachedWashroom,
    Integer availableBeds
) {
}