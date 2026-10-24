package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.dto.ClaimAssessmentResponse;
import com.app.insurance_management.entity.ClaimAssessment;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.mapper.ClaimAssessmentMapper;
import com.app.insurance_management.repository.ClaimAssessmentRepository;
import com.app.insurance_management.repository.ClaimDocumentRepository;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.service.ClaimAssessmentService;
import com.app.insurance_management.util.ClaimAssessmentCalculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimAssessmentServiceImpl
        implements ClaimAssessmentService {

    private final ClaimAssessmentRepository assessmentRepository;
    private final ClaimMasterRepository claimMasterRepository;
    private final ClaimAssessmentMapper assessmentMapper;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final ClaimDocumentRepository claimDocumentRepository;


    @Override
    @Transactional
    public ClaimAssessmentResponse createAssessment(Long claimId) {

        // 1. Fetch claim
        ClaimMaster claim = claimMasterRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + claimId));

// 2. Documents must be verified before assessment
        boolean hasDocuments = claimDocumentRepository.existsByClaim_ClaimId(claimId);
        boolean allDocumentsVerified = hasDocuments && claimDocumentRepository
                .findByClaim_ClaimId(claimId).stream()
                .allMatch(document -> "VERIFIED".equalsIgnoreCase(document.getVerificationStatus()));
        if (!allDocumentsVerified) {
            throw new IllegalStateException("Assessment is not allowed until all claim documents are verified");
        }

// 3. Assessment is not allowed during survey or investigation
        if (claim.getClaimStatus() == ClaimStatus.UNDER_SURVEY
                || claim.getClaimStatus() == ClaimStatus.UNDER_INVESTIGATION) {
            throw new IllegalStateException(
                    "Claim assessment is not allowed during survey or investigation");
        }

        // 2. Get estimated loss
        BigDecimal claimAmount = claim.getEstimatedLoss();

        if (claimAmount == null
                || claimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Estimated loss must be greater than zero");
        }

        BigDecimal deductible = new BigDecimal("10000");
        BigDecimal depreciation = new BigDecimal("5000");
        BigDecimal nonPayable = new BigDecimal("2000");

        // 3. Calculate net payable
        BigDecimal netPayable =
                ClaimAssessmentCalculation.calculateNetPayable(
                        claimAmount,
                        deductible,
                        depreciation,
                        nonPayable
                );

        // 4. Map values to entity
        ClaimAssessment assessment = assessmentMapper.toEntity(
                claim,
                claimAmount,
                deductible,
                depreciation,
                nonPayable,
                netPayable
        );

        ClaimAssessment saved =
                assessmentRepository.save(assessment);

// Assessment complete; move claim to approval stage
        claim.setClaimStatus(ClaimStatus.PENDING_APPROVAL);
        claimMasterRepository.save(claim);

// 6. Maintain claim history
        ClaimHistory history = ClaimHistory.builder()
                .claim(claim)  // ClaimMaster object, not claimId
                .action("Claim Assessment Completed")
                .performedBy(1L) // Replace with actual logged-in user ID
                .actionDate(LocalDateTime.now())
                .remarks(
                        "Assessment completed. Estimated loss: "
                                + claimAmount
                                + ", Net payable: "
                                + netPayable
                                + ". Claim moved to PENDING_APPROVAL."
                )
                .build();

        claimHistoryRepository.save(history);

// 7. Return response
        return assessmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimAssessmentResponse> getAssessmentsByClaimId(
            Long claimId) {

        if (!claimMasterRepository.existsById(claimId)) {
            throw new ClaimNotFoundException("Claim not found with ID: " + claimId);
        }

        List<ClaimAssessment> assessments =
                assessmentRepository.findByClaim_ClaimId(claimId);

        return assessments.stream()
                .map(assessmentMapper::toResponse)
                .toList();
    }
}