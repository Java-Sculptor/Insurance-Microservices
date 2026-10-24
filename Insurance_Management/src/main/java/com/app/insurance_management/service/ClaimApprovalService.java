package com.app.insurance_management.service;

import com.app.insurance_management.dto.ClaimApprovalRequest;
import com.app.insurance_management.dto.ClaimApprovalResponse;

import java.util.List;

public interface ClaimApprovalService {

    ClaimApprovalResponse approveOrRejectClaim(
            Long claimId,
            ClaimApprovalRequest request
    );

    public List<ClaimApprovalResponse> getAllClaims();
}
