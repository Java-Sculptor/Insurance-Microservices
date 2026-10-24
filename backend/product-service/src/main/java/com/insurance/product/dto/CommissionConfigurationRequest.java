
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CommissionConfigurationRequest(

        @NotNull(message = "Agent commission is required")
        @DecimalMin(value = "0.00", message = "Agent commission cannot be negative")
        @DecimalMax(value = "100.00", message = "Agent commission cannot exceed 100%")
        BigDecimal agentCommission,

        @NotNull(message = "Branch commission is required")
        @DecimalMin(value = "0.00", message = "Branch commission cannot be negative")
        @DecimalMax(value = "100.00", message = "Branch commission cannot exceed 100%")
        BigDecimal branchCommission,

        @NotNull(message = "Renewal commission is required")
        @DecimalMin(value = "0.00", message = "Renewal commission cannot be negative")
        @DecimalMax(value = "100.00", message = "Renewal commission cannot exceed 100%")
        BigDecimal renewalCommission

) {}
