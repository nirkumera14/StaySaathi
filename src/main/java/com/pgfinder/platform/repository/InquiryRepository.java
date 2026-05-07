package com.pgfinder.platform.repository;

import com.pgfinder.platform.domain.Inquiry;
import com.pgfinder.platform.domain.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    long countByStatus(InquiryStatus status);
}