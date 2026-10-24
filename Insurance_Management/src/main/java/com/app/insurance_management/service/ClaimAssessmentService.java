package com.app.insurance_management.service;

import com.app.insurance_management.dto.ClaimAssessmentRequest;
import com.app.insurance_management.dto.ClaimAssessmentResponse;

import java.util.List;

public interface ClaimAssessmentService {

    ClaimAssessmentResponse createAssessment(Long claimId);

    List<ClaimAssessmentResponse> getAssessmentsByClaimId(Long claimId);
}