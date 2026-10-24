package com.app.insurance_management.service;

import com.app.insurance_management.dto.InvestigationRequest;
import com.app.insurance_management.dto.InvestigationResponse;
import java.util.List;

public interface InvestigationService {
    InvestigationResponse startInvestigation(Long claimId, InvestigationRequest request);
    InvestigationResponse updateReport(Long claimId, Long investigationId, String reportPath);
    List<InvestigationResponse> getByClaim(Long claimId);
}
