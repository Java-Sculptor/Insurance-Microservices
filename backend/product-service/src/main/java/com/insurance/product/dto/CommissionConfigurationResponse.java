
package com.insurance.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CommissionConfigurationResponse(
        Long id,
        Long categoryId,
        String categoryName,
        BigDecimal agentCommission,
        BigDecimal branchCommission,
        BigDecimal renewalCommission,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
