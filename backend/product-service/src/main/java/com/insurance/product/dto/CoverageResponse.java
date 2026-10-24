package com.insurance.product.dto;

import java.math.BigDecimal;

public record CoverageResponse(
        Long id,
        Long productId,
        String coverageName,
        String description,
        BigDecimal minimumAmount,
        BigDecimal maximumAmount,
        boolean active
) {}