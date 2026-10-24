
package com.insurance.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EligibilityRuleResponse(
        Long id,
        Long productId,
        Integer minimumAge,
        Integer maximumAge,
        BigDecimal minimumIncome,
        String occupationCategory,
        Boolean medicalRequired,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
