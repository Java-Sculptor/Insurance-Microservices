
package com.insurance.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SumInsuredConfigurationRequest(

        @NotNull(message = "Minimum sum insured is required")
        @DecimalMin(
                value = "0.01",
                message = "Minimum sum insured must be greater than zero"
        )
        BigDecimal minimumSumInsured,

        @NotNull(message = "Maximum sum insured is required")
        @DecimalMin(
                value = "0.01",
                message = "Maximum sum insured must be greater than zero"
        )
        BigDecimal maximumSumInsured,

        @DecimalMin(
                value = "0.01",
                message = "Default sum insured must be greater than zero"
        )
        BigDecimal defaultSumInsured

) {}
