
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EligibilityRuleRequest(

        @NotNull(message = "Minimum age is required")
        @Min(value = 0, message = "Minimum age cannot be negative")
        @Max(value = 120, message = "Minimum age cannot exceed 120")
        Integer minimumAge,

        @NotNull(message = "Maximum age is required")
        @Min(value = 0, message = "Maximum age cannot be negative")
        @Max(value = 120, message = "Maximum age cannot exceed 120")
        Integer maximumAge,

        @NotNull(message = "Minimum income is required")
        @DecimalMin(value = "0.0", message = "Minimum income cannot be negative")
        BigDecimal minimumIncome,

        @NotBlank(message = "Occupation category is required")
        @Size(max = 100, message = "Occupation category cannot exceed 100 characters")
        String occupationCategory,

        @NotNull(message = "Medical required field is required")
        Boolean medicalRequired

) {
}
