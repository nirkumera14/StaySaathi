package com.pgfinder.platform.service;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.Inquiry;
import com.pgfinder.platform.domain.InquiryStatus;
import com.pgfinder.platform.domain.Listing;
import com.pgfinder.platform.domain.Review;
import com.pgfinder.platform.domain.RoomOption;
import com.pgfinder.platform.domain.SortBy;
import com.pgfinder.platform.dto.AdminInquiryDto;
import com.pgfinder.platform.dto.AdminMetricsDto;
import com.pgfinder.platform.dto.GooglePlaceDataDto;
import com.pgfinder.platform.dto.IndiaCityDto;
import com.pgfinder.platform.dto.IndiaCitySummaryDto;
import com.pgfinder.platform.dto.IndiaListingsResponseDto;
import com.pgfinder.platform.dto.InquiryRequest;
import com.pgfinder.platform.dto.ListingDetailDto;
import com.pgfinder.platform.dto.ListingSearchCriteria;
import com.pgfinder.platform.dto.ListingSummaryDto;
import com.pgfinder.platform.dto.PagedResponseDto;
import com.pgfinder.platform.dto.ReviewDto;
import com.pgfinder.platform.dto.ReviewRequest;
import com.pgfinder.platform.dto.RoomOptionDto;
import com.pgfinder.platform.repository.InquiryRepository;
import com.pgfinder.platform.repository.ListingCityAggregateProjection;
import com.pgfinder.platform.repository.ListingCityCountProjection;
import com.pgfinder.platform.repository.ListingRepository;
import com.pgfinder.platform.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class ListingService {

    private static final double MAX_FALLBACK_DISTANCE_KM = 40.0;

    private final ListingRepository listingRepository;
    private final ReviewRepository reviewRepository;
    private final InquiryRepository inquiryRepository;
    private final GooglePlacesService googlePlacesService;
    private final IndiaCityCatalogService indiaCityCatalogService;

    public ListingService(
        ListingRepository listingRepository,
        ReviewRepository reviewRepository,
        InquiryRepository inquiryRepository,
        GooglePlacesService googlePlacesService,
        IndiaCityCatalogService indiaCityCatalogService
    ) {
        this.listingRepository = listingRepository;
        this.reviewRepository = reviewRepository;
        this.inquiryRepository = inquiryRepository;
        this.googlePlacesService = googlePlacesService;
        this.indiaCityCatalogService = indiaCityCatalogService;
    }

    public PagedResponseDto<ListingSummaryDto> searchListings(ListingSearchCriteria criteria) {
        return searchListingsInternal(criteria);
    }

    public IndiaListingsResponseDto searchIndiaListings(ListingSearchCriteria criteria) {
        PagedResponseDto<ListingSummaryDto> initial = searchListingsInternal(criteria);
        String requestedCity = criteria.getCity() == null ? "" : criteria.getCity().trim();

        if (!StringUtils.hasText(requestedCity) || initial.total() > 0) {
            return toIndiaResponse(initial, false, null, null, null);
        }

        Map<String, ListingCityAggregate> cityAggregateMap = buildListingCityAggregateMap();
        FallbackCandidate fallback = findFallbackCity(requestedCity, cityAggregateMap);
        if (fallback == null) {
            return toIndiaResponse(initial, false, null, null, null);
        }

        ListingSearchCriteria fallbackCriteria = copyCriteria(criteria);
        fallbackCriteria.setCity(fallback.fallbackCity());
        PagedResponseDto<ListingSummaryDto> fallbackResults = searchListingsInternal(fallbackCriteria);

        return toIndiaResponse(
            fallbackResults,
            true,
            requestedCity,
            fallback.fallbackCity(),
            roundToSingleDecimal(fallback.distanceKm())
        );
    }

    public List<IndiaCityDto> searchIndiaCities(String query, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        String normalizedQuery = normalizeText(query);

        List<IndiaCityCatalogEntry> baseMatches = indiaCityCatalogService.search(query, Math.min(safeLimit * 8, 100));
        Map<String, Long> cityCountMap = buildCityCountMap();

        Comparator<IndiaCityDto> comparator;
        if (normalizedQuery.isBlank()) {
            comparator = Comparator
                .comparing(IndiaCityDto::hasListings).reversed()
                .thenComparing(IndiaCityDto::listingCount, Comparator.reverseOrder())
                .thenComparing(IndiaCityDto::city)
                .thenComparing(IndiaCityDto::state);
        } else {
            comparator = Comparator
                .comparing((IndiaCityDto dto) -> !normalizeText(dto.city()).startsWith(normalizedQuery))
                .thenComparing(dto -> !normalizeText(dto.label()).startsWith(normalizedQuery))
                .thenComparing(IndiaCityDto::hasListings).reversed()
                .thenComparing(IndiaCityDto::listingCount, Comparator.reverseOrder())
                .thenComparing(IndiaCityDto::city)
                .thenComparing(IndiaCityDto::state);
        }

        return baseMatches.stream()
            .map(entry -> {
                long listingCount = cityCountMap.getOrDefault(entry.normalizedCity(), 0L);
                return new IndiaCityDto(
                    entry.code(),
                    entry.city(),
                    entry.state(),
                    entry.label(),
                    listingCount > 0,
                    listingCount
                );
            })
            .sorted(comparator)
            .limit(safeLimit)
            .toList();
    }

    public List<IndiaCitySummaryDto> getIndiaCitySummary(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        List<ListingCityCountProjection> rows = listingRepository.findCityCounts(PageRequest.of(0, safeLimit));

        return rows.stream()
            .map(row -> {
                IndiaCityCatalogEntry cityInfo = indiaCityCatalogService.findFirstByCity(row.getCity());
                String state = cityInfo == null ? "" : cityInfo.state();
                long count = row.getListingCount() == null ? 0L : row.getListingCount();
                return new IndiaCitySummaryDto(row.getCity(), state, count);
            })
            .toList();
    }

    private PagedResponseDto<ListingSummaryDto> searchListingsInternal(ListingSearchCriteria criteria) {
        Specification<Listing> specification = buildSpecification(criteria);
        int safeSize = Math.min(Math.max(criteria.getSize(), 1), 60);
        int safePage = Math.max(criteria.getPage(), 0);

        if (criteria.getAmenities() != null && !criteria.getAmenities().isEmpty()) {
            List<Listing> full = listingRepository.findAll(specification, buildSort(criteria.getSortBy()));
            List<Listing> filtered = full.stream()
                .filter(listing -> listing.getAmenities().containsAll(criteria.getAmenities()))
                .toList();

            int start = Math.min(filtered.size(), safePage * safeSize);
            int end = Math.min(filtered.size(), start + safeSize);
            List<ListingSummaryDto> itemDtos = filtered.subList(start, end).stream()
                .map(this::toSummary)
                .toList();

            return new PagedResponseDto<>(itemDtos, filtered.size(), safePage, safeSize);
        }

        Pageable pageable = PageRequest.of(safePage, safeSize, buildSort(criteria.getSortBy()));
        Page<Listing> listingPage = listingRepository.findAll(specification, pageable);
        List<ListingSummaryDto> items = listingPage.getContent().stream()
            .map(this::toSummary)
            .toList();

        return new PagedResponseDto<>(items, listingPage.getTotalElements(), safePage, safeSize);
    }

    public List<String> getCitySuggestions(String query, int limit) {
        String safeQuery = query == null ? "" : query.trim();
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return listingRepository.findCitySuggestions(safeQuery, PageRequest.of(0, safeLimit));
    }

    public ListingDetailDto getListingBySlug(String slug) {
        Listing listing = listingRepository.findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        List<ReviewDto> reviews = reviewRepository.findByListingIdOrderByCreatedAtDesc(listing.getId()).stream()
            .map(this::toReviewDto)
            .toList();

        GooglePlaceDataDto googlePlaceData = googlePlacesService.fetchForListing(listing).orElse(null);
        if (!StringUtils.hasText(listing.getGooglePlaceId()) && googlePlaceData != null && StringUtils.hasText(googlePlaceData.placeId())) {
            listing.setGooglePlaceId(googlePlaceData.placeId());
            listingRepository.save(listing);
        }
        return toDetail(listing, reviews, googlePlaceData);
    }

    public List<ReviewDto> getReviewsBySlug(String slug) {
        Listing listing = listingRepository.findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
        return reviewRepository.findByListingIdOrderByCreatedAtDesc(listing.getId()).stream()
            .map(this::toReviewDto)
            .toList();
    }

    @Transactional
    public ReviewDto addReview(String slug, ReviewRequest request) {
        Listing listing = listingRepository.findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        Review review = new Review();
        review.setListing(listing);
        review.setReviewerName(request.getReviewerName());
        review.setOverallRating(request.getOverallRating());
        review.setLocationRating(request.getLocationRating());
        review.setStaffRating(request.getStaffRating());
        review.setFoodRating(request.getFoodRating());
        review.setCleanlinessRating(request.getCleanlinessRating());
        review.setWifiRating(request.getWifiRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        int existingCount = listing.getReviewCount() == null ? 0 : listing.getReviewCount();
        double existingAvg = listing.getRatingAvg() == null ? 0.0 : listing.getRatingAvg();
        double updatedAvg = ((existingAvg * existingCount) + request.getOverallRating()) / (existingCount + 1);

        listing.setReviewCount(existingCount + 1);
        listing.setRatingAvg(roundToSingleDecimal(updatedAvg));
        listingRepository.save(listing);

        return toReviewDto(saved);
    }

    @Transactional
    public void createInquiry(InquiryRequest request) {
        Listing listing = listingRepository.findById(request.getListingId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid listing id"));

        Inquiry inquiry = new Inquiry();
        inquiry.setListing(listing);
        inquiry.setName(request.getName());
        inquiry.setPhone(request.getPhone());
        inquiry.setEmail(request.getEmail());
        inquiry.setMoveInDate(request.getMoveInDate());
        inquiry.setPreferredRoomType(request.getPreferredRoomType());
        inquiry.setBudget(request.getBudget());
        inquiry.setExpectedStayMonths(request.getExpectedStayMonths());
        inquiry.setMessage(request.getMessage());
        inquiryRepository.save(inquiry);
    }

    public AdminMetricsDto getAdminMetrics() {
        long totalInquiries = inquiryRepository.count();
        return new AdminMetricsDto(
            listingRepository.count(),
            totalInquiries,
            inquiryRepository.countByStatus(InquiryStatus.NEW),
            inquiryRepository.countByStatus(InquiryStatus.CONTACTED),
            inquiryRepository.countByStatus(InquiryStatus.CLOSED)
        );
    }

    public List<AdminInquiryDto> getAllInquiries() {
        return inquiryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
            .map(inquiry -> new AdminInquiryDto(
                inquiry.getId(),
                inquiry.getListing().getId(),
                inquiry.getListing().getTitle(),
                inquiry.getName(),
                inquiry.getPhone(),
                inquiry.getEmail(),
                inquiry.getBudget(),
                inquiry.getExpectedStayMonths(),
                inquiry.getStatus(),
                inquiry.getCreatedAt()
            ))
            .toList();
    }

    @Transactional
    public void updateInquiryStatus(Long inquiryId, InquiryStatus status) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));
        inquiry.setStatus(status);
        inquiryRepository.save(inquiry);
    }

    private IndiaListingsResponseDto toIndiaResponse(
        PagedResponseDto<ListingSummaryDto> page,
        boolean fallbackApplied,
        String fallbackFromCity,
        String fallbackToCity,
        Double fallbackDistanceKm
    ) {
        return new IndiaListingsResponseDto(
            page.items(),
            page.total(),
            page.page(),
            page.size(),
            fallbackApplied,
            fallbackFromCity,
            fallbackToCity,
            fallbackDistanceKm
        );
    }

    private ListingSearchCriteria copyCriteria(ListingSearchCriteria original) {
        ListingSearchCriteria copy = new ListingSearchCriteria();
        copy.setQ(original.getQ());
        copy.setCity(original.getCity());
        copy.setGenderType(original.getGenderType());
        copy.setRoomType(original.getRoomType());
        copy.setMinRent(original.getMinRent());
        copy.setMaxRent(original.getMaxRent());
        copy.setFoodIncluded(original.getFoodIncluded());
        copy.setVerified(original.getVerified());
        Set<Amenity> amenities = original.getAmenities() == null ? Set.of() : Set.copyOf(original.getAmenities());
        copy.setAmenities(amenities);
        copy.setSortBy(original.getSortBy());
        copy.setPage(original.getPage());
        copy.setSize(original.getSize());
        return copy;
    }

    private Map<String, ListingCityAggregate> buildListingCityAggregateMap() {
        List<ListingCityAggregateProjection> rows = listingRepository.findCityAggregates();
        Map<String, ListingCityAggregate> aggregateMap = new HashMap<>();

        for (ListingCityAggregateProjection row : rows) {
            String city = row.getCity();
            if (!StringUtils.hasText(city)) {
                continue;
            }

            Long count = row.getListingCount();
            Double latitude = row.getLatitude();
            Double longitude = row.getLongitude();
            if (count == null || latitude == null || longitude == null) {
                continue;
            }

            String key = normalizeText(city);
            aggregateMap.put(key, new ListingCityAggregate(city.trim(), count, latitude, longitude));
        }

        return aggregateMap;
    }

    private Map<String, Long> buildCityCountMap() {
        Map<String, Long> cityCountMap = new HashMap<>();
        for (ListingCityAggregateProjection row : listingRepository.findCityAggregates()) {
            if (!StringUtils.hasText(row.getCity()) || row.getListingCount() == null) {
                continue;
            }
            cityCountMap.put(normalizeText(row.getCity()), row.getListingCount());
        }
        return cityCountMap;
    }

    private FallbackCandidate findFallbackCity(String requestedCity, Map<String, ListingCityAggregate> cityAggregateMap) {
        List<IndiaCityCatalogEntry> cityEntries = indiaCityCatalogService.findByCity(requestedCity);
        if (cityEntries.isEmpty() || cityAggregateMap.isEmpty()) {
            return null;
        }

        String requestedNormalized = normalizeText(requestedCity);
        FallbackCandidate best = null;

        for (IndiaCityCatalogEntry source : cityEntries) {
            for (ListingCityAggregate candidate : cityAggregateMap.values()) {
                if (normalizeText(candidate.city()).equals(requestedNormalized)) {
                    continue;
                }

                double distance = distanceKm(
                    source.latitude(),
                    source.longitude(),
                    candidate.latitude(),
                    candidate.longitude()
                );
                if (distance > MAX_FALLBACK_DISTANCE_KM) {
                    continue;
                }

                if (best == null || distance < best.distanceKm()) {
                    best = new FallbackCandidate(candidate.city(), distance);
                }
            }
        }

        return best;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }

    private Specification<Listing> buildSpecification(ListingSearchCriteria criteria) {
        return Specification
            .where(ListingSpecifications.titleOrLocationContains(criteria.getQ()))
            .and(ListingSpecifications.cityEquals(criteria.getCity()))
            .and(ListingSpecifications.genderEquals(criteria.getGenderType()))
            .and(ListingSpecifications.hasRoomType(criteria.getRoomType()))
            .and(ListingSpecifications.minRent(criteria.getMinRent()))
            .and(ListingSpecifications.maxRent(criteria.getMaxRent()))
            .and(ListingSpecifications.foodIncluded(criteria.getFoodIncluded()))
            .and(ListingSpecifications.verified(criteria.getVerified()));
    }

    private Sort buildSort(SortBy sortBy) {
        if (sortBy == null) {
            sortBy = SortBy.RELEVANCE;
        }

        return switch (sortBy) {
            case PRICE_LOW_TO_HIGH -> Sort.by(Sort.Direction.ASC, "startingPrice");
            case PRICE_HIGH_TO_LOW -> Sort.by(Sort.Direction.DESC, "startingPrice");
            case RATING -> Sort.by(Sort.Order.desc("ratingAvg"), Sort.Order.desc("reviewCount"));
            case RELEVANCE -> Sort.by(
                Sort.Order.desc("verified"),
                Sort.Order.desc("partnerVerified"),
                Sort.Order.desc("ratingAvg"),
                Sort.Order.asc("startingPrice")
            );
        };
    }

    private ListingSummaryDto toSummary(Listing listing) {
        List<String> topAmenities = listing.getAmenities().stream()
            .sorted(Comparator.comparing(Enum::name))
            .limit(4)
            .map(this::formatAmenity)
            .toList();

        return new ListingSummaryDto(
            listing.getId(),
            listing.getSlug(),
            listing.getTitle(),
            listing.getLocality(),
            listing.getCity(),
            listing.getGenderType(),
            listing.getStartingPrice(),
            listing.getEndingPrice(),
            listing.isFoodIncluded(),
            listing.isVerified(),
            listing.isPartnerVerified(),
            listing.isBrandNew(),
            listing.getRatingAvg(),
            listing.getReviewCount(),
            listing.getAvailableBeds(),
            listing.getMainImageUrl(),
            listing.getShortDescription(),
            topAmenities,
            listing.getNearbyMetro(),
            listing.getNearbyLandmark()
        );
    }

    private ListingDetailDto toDetail(Listing listing, List<ReviewDto> reviews, GooglePlaceDataDto googlePlaceData) {
        List<Amenity> amenityList = listing.getAmenities().stream()
            .sorted(Comparator.comparing(Enum::name))
            .toList();

        List<RoomOptionDto> roomOptionDtos = listing.getRoomOptions().stream()
            .sorted(Comparator.comparing(RoomOption::getPrice))
            .map(room -> new RoomOptionDto(
                room.getRoomType(),
                room.getLabel(),
                room.getPrice(),
                room.isAcIncluded(),
                room.isAttachedWashroom(),
                room.getAvailableBeds()
            ))
            .toList();

        return new ListingDetailDto(
            listing.getId(),
            listing.getSlug(),
            listing.getTitle(),
            listing.getCity(),
            listing.getLocality(),
            listing.getAddress(),
            listing.getShortDescription(),
            listing.getDescription(),
            listing.getBrandName(),
            listing.getGenderType(),
            listing.getStartingPrice(),
            listing.getEndingPrice(),
            listing.getSecurityDeposit(),
            listing.isFoodIncluded(),
            listing.isVerified(),
            listing.isPartnerVerified(),
            listing.isBrandNew(),
            listing.getAvailableBeds(),
            listing.getRatingAvg(),
            listing.getReviewCount(),
            listing.getNearbyMetro(),
            listing.getNearbyLandmark(),
            listing.getLatitude(),
            listing.getLongitude(),
            listing.getContactName(),
            listing.getContactPhone(),
            listing.getMainImageUrl(),
            listing.getGalleryImages(),
            amenityList,
            roomOptionDtos,
            reviews,
            googlePlaceData
        );
    }

    private ReviewDto toReviewDto(Review review) {
        return new ReviewDto(
            review.getId(),
            review.getReviewerName(),
            review.getOverallRating(),
            review.getLocationRating(),
            review.getStaffRating(),
            review.getFoodRating(),
            review.getCleanlinessRating(),
            review.getWifiRating(),
            review.getComment(),
            review.getCreatedAt(),
            "STAYSAATHI"
        );
    }

    private String formatAmenity(Amenity amenity) {
        return amenity.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
    }

    private double roundToSingleDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record ListingCityAggregate(String city, long listingCount, double latitude, double longitude) {
    }

    private record FallbackCandidate(String fallbackCity, double distanceKm) {
    }
}
