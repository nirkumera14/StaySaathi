package com.pgfinder.platform.controller;

import com.pgfinder.platform.domain.InquiryStatus;
import com.pgfinder.platform.dto.AdminInquiryDto;
import com.pgfinder.platform.dto.AdminMetricsDto;
import com.pgfinder.platform.dto.ApiMessageDto;
import com.pgfinder.platform.service.ListingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final ListingService listingService;

    @Value("${app.admin-key}")
    private String adminKey;

    public AdminApiController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/metrics")
    public AdminMetricsDto metrics(@RequestHeader(name = "X-Admin-Key", required = false) String incomingKey) {
        validateKey(incomingKey);
        return listingService.getAdminMetrics();
    }

    @GetMapping("/inquiries")
    public List<AdminInquiryDto> inquiries(@RequestHeader(name = "X-Admin-Key", required = false) String incomingKey) {
        validateKey(incomingKey);
        return listingService.getAllInquiries();
    }

    @PatchMapping("/inquiries/{id}/status")
    public ApiMessageDto updateStatus(
        @RequestHeader(name = "X-Admin-Key", required = false) String incomingKey,
        @PathVariable Long id,
        @RequestParam InquiryStatus status
    ) {
        validateKey(incomingKey);
        listingService.updateInquiryStatus(id, status);
        return new ApiMessageDto("Inquiry status updated");
    }

    private void validateKey(String incomingKey) {
        if (incomingKey == null || incomingKey.isBlank() || !incomingKey.equals(adminKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin key");
        }
    }
}