
package com.insurance.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PremiumFrequencyRequest(

        @NotBlank(message = "Premium frequency is required")
        @Pattern(
                regexp = "(?i)MONTHLY|QUARTERLY|HALF_YEARLY|YEARLY",
                message = "Frequency must be MONTHLY, QUARTERLY, HALF_YEARLY, or YEARLY"
        )
        String frequency

) {}
