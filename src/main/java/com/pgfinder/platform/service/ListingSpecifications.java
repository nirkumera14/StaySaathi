package com.pgfinder.platform.service;

import com.pgfinder.platform.domain.GenderType;
import com.pgfinder.platform.domain.Listing;
import com.pgfinder.platform.domain.RoomType;
import org.springframework.data.jpa.domain.Specification;

public final class ListingSpecifications {

    private ListingSpecifications() {
    }

    public static Specification<Listing> titleOrLocationContains(String queryText) {
        return (root, query, cb) -> {
            if (queryText == null || queryText.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + queryText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("locality")), pattern),
                cb.like(cb.lower(root.get("address")), pattern)
            );
        };
    }

    public static Specification<Listing> cityEquals(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("city")), city.toLowerCase());
        };
    }

    public static Specification<Listing> genderEquals(GenderType genderType) {
        return (root, query, cb) -> {
            if (genderType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("genderType"), genderType);
        };
    }

    public static Specification<Listing> minRent(Integer minRent) {
        return (root, query, cb) -> minRent == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("endingPrice"), minRent);
    }

    public static Specification<Listing> maxRent(Integer maxRent) {
        return (root, query, cb) -> maxRent == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("startingPrice"), maxRent);
    }

    public static Specification<Listing> foodIncluded(Boolean foodIncluded) {
        return (root, query, cb) -> foodIncluded == null ? cb.conjunction() : cb.equal(root.get("foodIncluded"), foodIncluded);
    }

    public static Specification<Listing> verified(Boolean verified) {
        return (root, query, cb) -> verified == null ? cb.conjunction() : cb.equal(root.get("verified"), verified);
    }

    public static Specification<Listing> hasRoomType(RoomType roomType) {
        return (root, query, cb) -> {
            if (roomType == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            return cb.equal(root.join("roomOptions").get("roomType"), roomType);
        };
    }
}
