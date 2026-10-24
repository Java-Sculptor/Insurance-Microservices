package com.app.insurance_management.controller;

import com.app.insurance_management.apiResponse.ApiResponse;
import com.app.insurance_management.dto.ClaimEligibilityResponse;
import com.app.insurance_management.serviceImpl.ClaimEligibilityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Read-only endpoint to check eligibility without registering a claim. */
@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimEligibilityController {
    private static final Logger log = LoggerFactory.getLogger(ClaimEligibilityController.class);
    private final ClaimEligibilityService eligibilityService;

    @GetMapping("/policy-eligibility")
    public ResponseEntity<ApiResponse<ClaimEligibilityResponse>> validatePolicy(
            @RequestParam Long policyId, @RequestParam Long customerId,
            @RequestParam LocalDate incidentDate, @RequestParam String claimType,
            @RequestParam BigDecimal estimatedLoss) {
        log.info("Checking claim eligibility for policyId={}, customerId={}", policyId, customerId);
        ClaimEligibilityResponse result = eligibilityService.validate(policyId, customerId, incidentDate, claimType, estimatedLoss);
        return ResponseEntity.ok(ApiResponse.<ClaimEligibilityResponse>builder()
            .success(result.eligible()).message(result.eligible() ? "Claim is eligible" : "Claim is not eligible")
            .data(result).build());
    }
}
