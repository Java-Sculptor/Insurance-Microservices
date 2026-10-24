package com.app.insurance_management.service;

import com.app.insurance_management.dto.ClaimRequest;
import com.app.insurance_management.dto.ClaimResponse;

import java.util.List;

public interface ClaimService {

    ClaimResponse claimRegister(ClaimRequest claimRequest);

    List<ClaimResponse> getAllClaims();

    ClaimResponse getClaimDetails(Long claimId);
}
