package com.app.insurance_management.dto;

import com.app.insurance_management.enums.ClaimStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimApprovalResponse {

    private Long claimId;
    private String claimNumber;
    private ClaimStatus claimStatus;
    private String reason;
}