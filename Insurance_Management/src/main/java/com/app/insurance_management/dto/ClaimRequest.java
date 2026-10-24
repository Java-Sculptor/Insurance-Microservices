
package com.app.insurance_management.dto;

import com.app.insurance_management.enums.ClaimType;
import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimRequest {

    @NotNull(message = "Policy ID is required")
    @Positive(message = "Policy ID must be positive")
    private Long policyId;
    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;
    @NotNull(message = "Claim type is required")
    private ClaimType claimType;
    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    private LocalDate incidentDate;
    @NotNull(message = "Estimated loss is required")
    @DecimalMin(value = "0.01", message = "Estimated loss must be greater than zero")
    private BigDecimal estimatedLoss;
}
