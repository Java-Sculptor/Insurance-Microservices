
package com.app.insurance_management.dto;

import com.app.insurance_management.enums.ClaimType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimResponse {

    private Long claimId;
    private String claimNumber;
    private Long policyId;
    private Long customerId;
    private ClaimType claimType;
    private LocalDate incidentDate;
    private LocalDate claimDate;
    private BigDecimal estimatedLoss;
    private String claimStatus;
}
