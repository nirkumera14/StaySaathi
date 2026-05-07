package com.pgfinder.platform.controller;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.GenderType;
import com.pgfinder.platform.domain.RoomType;
import com.pgfinder.platform.domain.SortBy;
import com.pgfinder.platform.dto.ApiMessageDto;
import com.pgfinder.platform.dto.FilterOptionsDto;
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
import com.pgfinder.platform.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ListingApiController {

    private final ListingService listingService;

    public ListingApiController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/listings")
    public PagedResponseDto<ListingSummaryDto> searchListings(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) GenderType gender,
        @RequestParam(required = false) RoomType roomType,
        @RequestParam(required = false) Integer minRent,
        @RequestParam(required = false) Integer maxRent,
        @RequestParam(required = false) Boolean foodIncluded,
        @RequestParam(required = false) Boolean verified,
        @RequestParam(required = false) Set<Amenity> amenities,
        @RequestParam(required = false, defaultValue = "RELEVANCE") SortBy sort,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "18") int size
    ) {
        return listingService.searchListings(buildCriteria(
            q, city, gender, roomType, minRent, maxRent, foodIncluded, verified, amenities, sort, page, size
        ));
    }

    @GetMapping("/listings/india")
    public IndiaListingsResponseDto searchIndiaListings(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) GenderType gender,
        @RequestParam(required = false) RoomType roomType,
        @RequestParam(required = false) Integer minRent,
        @RequestParam(required = false) Integer maxRent,
        @RequestParam(required = false) Boolean foodIncluded,
        @RequestParam(required = false) Boolean verified,
        @RequestParam(required = false) Set<Amenity> amenities,
        @RequestParam(required = false, defaultValue = "RELEVANCE") SortBy sort,
        @RequestParam(required = false, defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "18") int size
    ) {
        return listingService.searchIndiaListings(buildCriteria(
            q, city, gender, roomType, minRent, maxRent, foodIncluded, verified, amenities, sort, page, size
        ));
    }

    @GetMapping("/cities")
    public List<String> citySuggestions(
        @RequestParam(required = false) String query,
        @RequestParam(required = false, defaultValue = "8") int limit
    ) {
        return listingService.getCitySuggestions(query, limit);
    }

    @GetMapping("/cities/india")
    public List<IndiaCityDto> indiaCitySuggestions(
        @RequestParam(required = false) String query,
        @RequestParam(required = false, defaultValue = "8") int limit
    ) {
        return listingService.searchIndiaCities(query, limit);
    }

    @GetMapping("/cities/summary")
    public List<IndiaCitySummaryDto> indiaCitySummary(
        @RequestParam(required = false, defaultValue = "8") int limit
    ) {
        return listingService.getIndiaCitySummary(limit);
    }

    @GetMapping("/meta/options")
    public FilterOptionsDto filterOptions() {
        return new FilterOptionsDto(
            Arrays.stream(GenderType.values())
                .map(Enum::name)
                .toList(),
            Arrays.stream(RoomType.values())
                .map(Enum::name)
                .toList(),
            Arrays.stream(SortBy.values())
                .map(Enum::name)
                .toList(),
            Arrays.stream(Amenity.values())
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList()
        );
    }

    @GetMapping("/listings/{slug}")
    public ListingDetailDto getListing(@PathVariable String slug) {
        return listingService.getListingBySlug(slug);
    }

    @GetMapping("/listings/{slug}/reviews")
    public List<ReviewDto> getReviews(@PathVariable String slug) {
        return listingService.getReviewsBySlug(slug);
    }

    @PostMapping("/listings/{slug}/reviews")
    public ReviewDto addReview(@PathVariable String slug, @Valid @RequestBody ReviewRequest request) {
        return listingService.addReview(slug, request);
    }

    @PostMapping("/inquiries")
    public ApiMessageDto createInquiry(@Valid @RequestBody InquiryRequest request) {
        listingService.createInquiry(request);
        return new ApiMessageDto("Inquiry submitted. The owner will contact you shortly.");
    }

    private ListingSearchCriteria buildCriteria(
        String q,
        String city,
        GenderType gender,
        RoomType roomType,
        Integer minRent,
        Integer maxRent,
        Boolean foodIncluded,
        Boolean verified,
        Set<Amenity> amenities,
        SortBy sort,
        int page,
        int size
    ) {
        ListingSearchCriteria criteria = new ListingSearchCriteria();
        criteria.setQ(q);
        criteria.setCity(city);
        criteria.setGenderType(gender);
        criteria.setRoomType(roomType);
        criteria.setMinRent(minRent);
        criteria.setMaxRent(maxRent);
        criteria.setFoodIncluded(foodIncluded);
        criteria.setVerified(verified);
        criteria.setAmenities(amenities == null ? Set.of() : amenities);
        criteria.setSortBy(sort);
        criteria.setPage(page);
        criteria.setSize(size);
        return criteria;
    }
}
