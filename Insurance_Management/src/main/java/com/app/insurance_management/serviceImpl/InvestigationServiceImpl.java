package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.dto.InvestigationRequest;
import com.app.insurance_management.dto.InvestigationResponse;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.Investigation;
import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.exception.BusinessRuleException;
import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.repository.InvestigationRepository;
import com.app.insurance_management.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** Coordinates investigation lifecycle and records each state change in claim history. */
@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private static final Logger log = LoggerFactory.getLogger(InvestigationServiceImpl.class);
    private final InvestigationRepository investigationRepository;
    private final ClaimMasterRepository claimMasterRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    @Override
    @Transactional
    public InvestigationResponse startInvestigation(Long claimId, InvestigationRequest request) {
        ClaimMaster claim = claimMasterRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + claimId));
        // Investigation is allowed only after registration/verification or when explicitly sent for investigation.
        if (claim.getClaimStatus() != ClaimStatus.REGISTERED
                && claim.getClaimStatus() != ClaimStatus.PENDING_VERIFICATION
                && claim.getClaimStatus() != ClaimStatus.UNDER_SURVEY
                && claim.getClaimStatus() != ClaimStatus.UNDER_INVESTIGATION) {
            throw new BusinessRuleException("Investigation cannot start when claim status is " + claim.getClaimStatus());
        }
        Investigation investigation = new Investigation();
        investigation.setClaim(claim);
        investigation.setInvestigatorId(request.getInvestigatorId());
        investigation.setInvestigationReason(request.getInvestigationReason().trim());
        investigation.setInvestigationStatus(ClaimStatus.UNDER_INVESTIGATION);
        investigation.setCreatedAt(LocalDateTime.now());
        Investigation saved = investigationRepository.save(investigation);
        claim.setClaimStatus(ClaimStatus.UNDER_INVESTIGATION);
        claimMasterRepository.save(claim);
        saveHistory(claim, "INVESTIGATION_STARTED", "Investigation " + saved.getInvestigationId() + " assigned to investigator " + saved.getInvestigatorId());
        log.info("Investigation started: investigationId={}, claimId={}, investigatorId={}", saved.getInvestigationId(), claimId, saved.getInvestigatorId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public InvestigationResponse updateReport(Long claimId, Long investigationId, String reportPath) {
        if (reportPath == null || reportPath.isBlank()) throw new BusinessRuleException("Report path is required");
        Investigation investigation = investigationRepository.findById(investigationId)
            .orElseThrow(() -> new ClaimNotFoundException("Investigation not found with ID: " + investigationId));
        if (!investigation.getClaim().getClaimId().equals(claimId)) {
            throw new BusinessRuleException("Investigation does not belong to the supplied claim");
        }
        if (investigation.getInvestigationStatus() != ClaimStatus.UNDER_INVESTIGATION) {
            throw new BusinessRuleException("Report can only be submitted for an investigation with status UNDER_INVESTIGATION");
        }
        investigation.setReportPath(reportPath.trim());
        investigation.setInvestigationStatus(ClaimStatus.INVESTIGATION_COMPLETED);
        investigation.setUpdatedAt(LocalDateTime.now());
        Investigation saved = investigationRepository.save(investigation);
        ClaimMaster claim = saved.getClaim();
        // A submitted report completes investigation and advances the claim to assessment-ready status.
        claim.setClaimStatus(ClaimStatus.INVESTIGATION_COMPLETED);
        claimMasterRepository.save(claim);
        saveHistory(claim, "INVESTIGATION_REPORT_SUBMITTED", "Investigation report submitted for investigation " + investigationId + "; claim moved to INVESTIGATION_COMPLETED");
        log.info("Investigation report submitted: investigationId={}, claimId={}", investigationId, claim.getClaimId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestigationResponse> getByClaim(Long claimId) {
        if (!claimMasterRepository.existsById(claimId)) throw new ClaimNotFoundException("Claim not found with ID: " + claimId);
        return investigationRepository.findByClaim_ClaimId(claimId).stream().map(this::toResponse).toList();
    }

    private void saveHistory(ClaimMaster claim, String action, String remarks) {
        claimHistoryRepository.save(ClaimHistory.builder().claim(claim).action(action).actionDate(LocalDateTime.now()).remarks(remarks).build());
    }
    private InvestigationResponse toResponse(Investigation entity) {
        InvestigationResponse response = new InvestigationResponse();
        response.setInvestigationId(entity.getInvestigationId());
        response.setClaimId(entity.getClaim().getClaimId());
        response.setInvestigatorId(entity.getInvestigatorId());
        response.setInvestigationReason(entity.getInvestigationReason());
        response.setReportPath(entity.getReportPath());
        response.setInvestigationStatus(entity.getInvestigationStatus());
        return response;
    }
}
