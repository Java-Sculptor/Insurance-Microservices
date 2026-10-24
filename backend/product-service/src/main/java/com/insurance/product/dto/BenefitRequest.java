
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BenefitRequest(

        @NotBlank(message = "Benefit name is required")
        @Size(max = 150, message = "Benefit name cannot exceed 150 characters")
        String benefitName,

        String description,

        @DecimalMin(
                value = "0.01",
                message = "Coverage amount must be greater than zero"
        )
        BigDecimal coverageAmount

) {
}
