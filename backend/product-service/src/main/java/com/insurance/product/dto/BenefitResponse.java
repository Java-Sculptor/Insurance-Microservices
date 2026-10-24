package com.insurance.product.dto;

import java.math.BigDecimal;

public record BenefitResponse(
        Long id,
        Long productId,
        String benefitName,
        String description,
        BigDecimal coverageAmount,
        boolean active
) {
}