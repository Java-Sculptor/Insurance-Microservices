
package com.insurance.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(

        Long id,
        String productCode,
        String productName,
        String productType,
        String description,

        Long categoryId,
        String categoryName,

        Integer minAge,
        Integer maxAge,

        BigDecimal minSumInsured,
        BigDecimal maxSumInsured,

        Integer minPolicyTerm,
        Integer maxPolicyTerm,

        String premiumFrequency,
        Integer waitingPeriodDays,
        Integer gracePeriodDays,
        BigDecimal gstPercentage,

        String status,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
