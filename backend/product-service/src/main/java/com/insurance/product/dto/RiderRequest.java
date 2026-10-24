
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RiderRequest(

        @NotBlank(message = "Rider name is required")
        @Size(max = 150, message = "Rider name cannot exceed 150 characters")
        String riderName,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Additional premium is required")
        @DecimalMin(
                value = "0.00",
                message = "Additional premium cannot be negative"
        )
        BigDecimal additionalPremium

) {
}
