package com.app.insurance_management.dto;

import java.math.BigDecimal;

public record PremiumEligibilityResponse(
        Long policyId,
        boolean premiumUpToDate,
        BigDecimal outstandingAmount,
        String lastPaymentStatus
) {}