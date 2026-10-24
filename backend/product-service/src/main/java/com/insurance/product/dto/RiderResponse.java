
package com.insurance.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiderResponse(
        Long id,
        Long productId,
        String riderName,
        String description,
        BigDecimal additionalPremium,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
