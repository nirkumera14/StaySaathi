package com.pgfinder.platform.service;

public record IndiaCityCatalogEntry(
    String code,
    String city,
    String state,
    double latitude,
    double longitude,
    String normalizedCity,
    String normalizedLabel
) {
    public String label() {
        return city + ", " + state;
    }
}
