package com.app.insurance_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyEligibilityResponse(
        Long policyId,
        String policyNumber,
        Long customerId,
        String policyStatus,
        LocalDate effectiveDate,
        LocalDate expiryDate,
        boolean coverageAvailable,
        boolean waitingPeriodCompleted,
        BigDecimal remainingClaimLimit
) {}