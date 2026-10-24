
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product code is required")
        @Size(max = 30, message = "Product code cannot exceed 30 characters")
        String productCode,

        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Product name cannot exceed 150 characters")
        String productName,

        @NotNull(message = "Product category ID is required")
        @Positive(message = "Product category ID must be positive")
        Long categoryId,

        @NotBlank(message = "Product type is required")
        @Size(max = 50, message = "Product type cannot exceed 50 characters")
        String productType,

        String description,

        @Min(value = 0, message = "Minimum age cannot be negative")
        @Max(value = 120, message = "Minimum age cannot exceed 120")
        Integer minAge,

        @Min(value = 0, message = "Maximum age cannot be negative")
        @Max(value = 120, message = "Maximum age cannot exceed 120")
        Integer maxAge,

        @DecimalMin(value = "0.01", message = "Minimum sum insured must be positive")
        BigDecimal minSumInsured,

        @DecimalMin(value = "0.01", message = "Maximum sum insured must be positive")
        BigDecimal maxSumInsured,

        @Positive(message = "Minimum policy term must be positive")
        Integer minPolicyTerm,

        @Positive(message = "Maximum policy term must be positive")
        Integer maxPolicyTerm,

        @Size(max = 30, message = "Premium frequency cannot exceed 30 characters")
        String premiumFrequency,

        @Min(value = 0, message = "Waiting period cannot be negative")
        Integer waitingPeriodDays,

        @Min(value = 0, message = "Grace period cannot be negative")
        Integer gracePeriodDays,

        @DecimalMin(value = "0.00", message = "GST cannot be negative")
        @DecimalMax(value = "100.00", message = "GST cannot exceed 100%")
        BigDecimal gstPercentage

) {
}
