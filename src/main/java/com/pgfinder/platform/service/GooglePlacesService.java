package com.pgfinder.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgfinder.platform.domain.Listing;
import com.pgfinder.platform.dto.GooglePlaceDataDto;
import com.pgfinder.platform.dto.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GooglePlacesService {

    private static final Logger log = LoggerFactory.getLogger(GooglePlacesService.class);

    private static final Duration PLACE_ID_TTL = Duration.ofHours(12);

    private final RestClient restClient;
    private final String apiKey;
    private final boolean enabled;

    private final Map<String, CacheEntry<String>> placeIdCache = new ConcurrentHashMap<>();

    public GooglePlacesService(
        @Value("${app.google-maps.api-key:}") String apiKey,
        @Value("${app.google-maps.enabled:false}") boolean enabled
    ) {
        this.restClient = RestClient.builder().build();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.enabled = enabled;
    }

    public Optional<GooglePlaceDataDto> fetchForListing(Listing listing) {
        if (!enabled || apiKey.isBlank()) {
            return Optional.empty();
        }

        try {
            String placeId = resolvePlaceId(listing);
            if (!StringUtils.hasText(placeId)) {
                return Optional.empty();
            }
            return fetchPlaceDetails(placeId);
        } catch (Exception ex) {
            log.debug("Google Places lookup failed for listing {}", listing.getSlug(), ex);
            return Optional.empty();
        }
    }

    private String resolvePlaceId(Listing listing) {
        if (StringUtils.hasText(listing.getGooglePlaceId())) {
            return listing.getGooglePlaceId();
        }

        String cacheKey = (listing.getTitle() + "|" + listing.getAddress() + "|" + listing.getCity()).toLowerCase(Locale.ENGLISH);
        String cached = getCachedValue(placeIdCache, cacheKey);
        if (StringUtils.hasText(cached)) {
            return cached;
        }

        List<String> searchParts = new ArrayList<>();
        if (StringUtils.hasText(listing.getTitle())) {
            searchParts.add(listing.getTitle());
        }
        if (StringUtils.hasText(listing.getAddress())) {
            searchParts.add(listing.getAddress());
        }
        if (StringUtils.hasText(listing.getLocality())) {
            searchParts.add(listing.getLocality());
        }
        if (StringUtils.hasText(listing.getCity())) {
            searchParts.add(listing.getCity());
        }

        String input = String.join(", ", searchParts);

        if (!StringUtils.hasText(input)) {
            return null;
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
            .queryParam("input", input)
            .queryParam("inputtype", "textquery")
            .queryParam("fields", "place_id")
            .queryParam("key", apiKey)
            .toUriString();

        JsonNode payload = restClient.get().uri(url).retrieve().body(JsonNode.class);
        if (payload == null || !"OK".equalsIgnoreCase(payload.path("status").asText())) {
            return null;
        }

        String placeId = payload.path("candidates").path(0).path("place_id").asText("");
        if (!StringUtils.hasText(placeId)) {
            return null;
        }

        placeIdCache.put(cacheKey, new CacheEntry<>(placeId, Instant.now().plus(PLACE_ID_TTL)));
        return placeId;
    }

    private Optional<GooglePlaceDataDto> fetchPlaceDetails(String placeId) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/details/json")
            .queryParam("place_id", placeId)
            .queryParam("fields", "name,url,rating,user_ratings_total,reviews,photos")
            .queryParam("reviews_sort", "newest")
            .queryParam("key", apiKey)
            .toUriString();

        JsonNode payload = restClient.get().uri(url).retrieve().body(JsonNode.class);
        if (payload == null || !"OK".equalsIgnoreCase(payload.path("status").asText())) {
            return Optional.empty();
        }

        JsonNode result = payload.path("result");
        if (result.isMissingNode()) {
            return Optional.empty();
        }

        Double rating = result.hasNonNull("rating") ? result.path("rating").asDouble() : null;
        Integer totalRatings = result.hasNonNull("user_ratings_total") ? result.path("user_ratings_total").asInt() : null;

        List<String> photoUrls = new ArrayList<>();
        Set<String> attributions = new LinkedHashSet<>();

        for (JsonNode attribution : payload.path("html_attributions")) {
            String text = attribution.asText("");
            if (StringUtils.hasText(text)) {
                attributions.add(text);
            }
        }

        for (JsonNode photo : result.path("photos")) {
            if (photoUrls.size() >= 8) {
                break;
            }
            String photoReference = photo.path("photo_reference").asText("");
            if (StringUtils.hasText(photoReference)) {
                photoUrls.add(buildPhotoUrl(photoReference));
            }

            for (JsonNode attribution : photo.path("html_attributions")) {
                String text = attribution.asText("");
                if (StringUtils.hasText(text)) {
                    attributions.add(text);
                }
            }
        }

        List<ReviewDto> reviews = new ArrayList<>();
        for (JsonNode reviewNode : result.path("reviews")) {
            if (reviews.size() >= 6) {
                break;
            }
            reviews.add(toGoogleReview(reviewNode));
        }

        String mapsUrl = result.path("url").asText("");
        if (!StringUtils.hasText(mapsUrl)) {
            mapsUrl = "https://www.google.com/maps/place/?q=place_id:" + placeId;
        }

        return Optional.of(new GooglePlaceDataDto(placeId, mapsUrl, rating, totalRatings, photoUrls, reviews, new ArrayList<>(attributions)));
    }

    private ReviewDto toGoogleReview(JsonNode node) {
        String reviewer = node.path("author_name").asText("Google User");
        Double rating = node.hasNonNull("rating") ? node.path("rating").asDouble() : null;
        String comment = node.path("text").asText("");
        if (node.path("translated").asBoolean(false) && StringUtils.hasText(comment)) {
            comment = comment + " (Translated by Google)";
        }
        long unixTime = node.path("time").asLong(0L);
        Instant createdAt = unixTime > 0L ? Instant.ofEpochSecond(unixTime) : null;

        return new ReviewDto(
            null,
            reviewer,
            rating,
            null,
            null,
            null,
            null,
            null,
            comment,
            createdAt,
            "GOOGLE_MAPS"
        );
    }

    private String buildPhotoUrl(String photoReference) {
        return UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/photo")
            .queryParam("maxwidth", 1200)
            .queryParam("photo_reference", photoReference)
            .queryParam("key", apiKey)
            .toUriString();
    }

    private <T> T getCachedValue(Map<String, CacheEntry<T>> cache, String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.expiresAt().isBefore(Instant.now())) {
            cache.remove(key);
            return null;
        }

        return entry.value();
    }

    private record CacheEntry<T>(T value, Instant expiresAt) {
    }
}
