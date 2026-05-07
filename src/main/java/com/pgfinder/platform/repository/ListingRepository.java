package com.pgfinder.platform.repository;

import com.pgfinder.platform.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    Optional<Listing> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
        select distinct l.city
        from Listing l
        where (:query is null or :query = '' or lower(l.city) like lower(concat('%', :query, '%')))
        order by l.city asc
    """)
    List<String> findCitySuggestions(@Param("query") String query, Pageable pageable);

    @Query("""
        select
            l.city as city,
            count(l) as listingCount,
            avg(l.latitude) as latitude,
            avg(l.longitude) as longitude
        from Listing l
        group by l.city
        order by count(l) desc, l.city asc
    """)
    List<ListingCityAggregateProjection> findCityAggregates();

    @Query("""
        select
            l.city as city,
            count(l) as listingCount
        from Listing l
        group by l.city
        order by count(l) desc, l.city asc
    """)
    List<ListingCityCountProjection> findCityCounts(Pageable pageable);
}
