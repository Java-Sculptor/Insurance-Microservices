
package com.insurance.product.dto;

import java.time.LocalDateTime;

public record PremiumFrequencyResponse(
        Long id,
        Long productId,
        String frequency,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
