package com.pgfinder.platform.repository;

import com.pgfinder.platform.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByListingIdOrderByCreatedAtDesc(Long listingId);
}