
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CoverageRequest(

        @NotBlank(message = "Coverage name is required")
        @Size(max = 100, message = "Coverage name cannot exceed 100 characters")
        String coverageName,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Minimum amount is required")
        @DecimalMin(value = "0.0", message = "Minimum amount cannot be negative")
        BigDecimal minimumAmount,

        @NotNull(message = "Maximum amount is required")
        @DecimalMin(value = "0.0", message = "Maximum amount cannot be negative")
        BigDecimal maximumAmount
) {}
