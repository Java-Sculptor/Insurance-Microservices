
package com.insurance.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SumInsuredConfigurationResponse(
        Long id,
        Long productId,
        BigDecimal minimumSumInsured,
        BigDecimal maximumSumInsured,
        BigDecimal defaultSumInsured,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
