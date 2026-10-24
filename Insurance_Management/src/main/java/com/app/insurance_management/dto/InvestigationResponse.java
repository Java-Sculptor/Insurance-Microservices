package com.app.insurance_management.dto;

import com.app.insurance_management.enums.ClaimStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestigationResponse {
    private Long investigationId;
    private Long claimId;
    private Long investigatorId;
    private String investigationReason;
    private String reportPath;
    private ClaimStatus investigationStatus;
}
