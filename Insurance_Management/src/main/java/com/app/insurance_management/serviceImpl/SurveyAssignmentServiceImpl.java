
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.dto.SurveyAssignmentRequest;
import com.app.insurance_management.dto.SurveyAssignmentResponse;
import com.app.insurance_management.dto.SurveyReportRequest;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.SurveyAssignment;
import com.app.insurance_management.enums.ClaimType;
import com.app.insurance_management.enums.SurveyStatus;
import com.app.insurance_management.mapper.SurveyAssignmentMapper;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.repository.SurveyAssignmentRepository;
import com.app.insurance_management.service.SurveyAssignmentService;
import com.app.insurance_management.service.SurveyPdfService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyAssignmentServiceImpl implements SurveyAssignmentService {

    private final SurveyAssignmentRepository surveyRepository;
    private final ClaimMasterRepository claimRepository;
    private final SurveyAssignmentMapper surveyAssignmentMapper;
    private final SurveyPdfService surveyPdfService;

    private final ClaimHistoryRepository claimHistoryRepository;

    @Value("${claim.survey.motor-threshold:100000}")
    private BigDecimal motorSurveyThreshold;

    // 1. Assign surveyor
    @Override
    @Transactional
    public SurveyAssignmentResponse assignSurveyor(
            Long claimId,
            SurveyAssignmentRequest request) {

        ClaimMaster claim = claimRepository.findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found: " + claimId));

        if (request == null
                || request.getSurveyorId() == null
                || request.getSurveyorId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid surveyor ID is required");
        }

        BigDecimal estimatedLoss = claim.getEstimatedLoss();

        boolean surveyRequired =
                claim.getClaimType() == ClaimType.VEHICLE
                        && estimatedLoss != null
                        && estimatedLoss.compareTo(
                        motorSurveyThreshold) > 0;



        if (!surveyRequired) {
            throw new IllegalStateException(
                    "Mandatory survey assignment is not required "
                            + "for this claim under the configured threshold");
        }

        List<SurveyAssignment> existingAssignments =
                surveyRepository.findByClaim_ClaimId(claimId);

        boolean activeAssignmentExists =
                existingAssignments.stream().anyMatch(assignment ->
                        assignment.getSurveyStatus()
                                == SurveyStatus.ASSIGNED
                                || assignment.getSurveyStatus()
                                == SurveyStatus.INSPECTION_IN_PROGRESS);

        if (activeAssignmentExists) {
            throw new IllegalStateException(
                    "An active survey assignment already exists");
        }

        SurveyAssignment assignment =
                surveyAssignmentMapper.toEntity(claim, request);

        SurveyAssignment saved =
                surveyRepository.save(assignment);


// Maintain claim history
        ClaimHistory history = ClaimHistory.builder()
                .claim(claim)
                .action("Surveyor Assigned")
                .performedBy(1L) // Replace with actual logged-in user ID
                .actionDate(LocalDateTime.now())
                .remarks(
                        "Surveyor ID " + request.getSurveyorId()
                                + " assigned to claim "
                                + claim.getClaimNumber()
                )
                .build();

        claimHistoryRepository.save(history);

        return surveyAssignmentMapper.toResponse(saved);


    }

    // 2. Submit survey findings and generate PDF
    @Override
    @Transactional
    public SurveyAssignmentResponse submitSurveyReport(
            Long claimId,
            Long surveyId,
            SurveyReportRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Survey report details are required");
        }

        if (request.getDamageDescription() == null
                || request.getDamageDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Damage description is required");
        }

        if (request.getAssessedDamageAmount() == null
                || request.getAssessedDamageAmount().signum() < 0) {
            throw new IllegalArgumentException(
                    "A valid assessed damage amount is required");
        }

        if (request.getRecommendation() == null
                || request.getRecommendation().isBlank()) {
            throw new IllegalArgumentException(
                    "Surveyor recommendation is required");
        }

        ClaimMaster claim = claimRepository.findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found: " + claimId));

        SurveyAssignment assignment =
                surveyRepository.findById(surveyId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Survey assignment not found: "
                                                + surveyId));

        // Verify the survey belongs to this claim
        if (!assignment.getClaim().getClaimId()
                .equals(claim.getClaimId())) {
            throw new IllegalArgumentException(
                    "Survey assignment does not belong to this claim");
        }

        if (assignment.getSurveyStatus() == SurveyStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot submit report for a cancelled assignment");
        }

        if (assignment.getSurveyStatus()
                == SurveyStatus.REPORT_SUBMITTED) {
            throw new IllegalStateException(
                    "Survey report has already been submitted");
        }

        // Save surveyor findings to the entity
        assignment.setDamageDescription(
                request.getDamageDescription().trim());

        assignment.setAssessedDamageAmount(
                request.getAssessedDamageAmount());

        assignment.setSurveyorRemarks(
                request.getSurveyorRemarks() == null
                        ? null
                        : request.getSurveyorRemarks().trim());

        assignment.setRecommendation(
                request.getRecommendation().trim());

        // Generate PDF using the separate PDF service
        String reportPath = surveyPdfService.generateSurveyPdf(
                claim,
                assignment
        );

        // Save generated PDF path and update status
        assignment.setReportPath(reportPath);
        assignment.setSurveyStatus(SurveyStatus.REPORT_SUBMITTED);

        SurveyAssignment saved =
                surveyRepository.save(assignment);


// Maintain claim history
        ClaimHistory history = ClaimHistory.builder()
                .claim(claim)
                .action("Survey Report Submitted")
                .performedBy(1L) // Replace with actual logged-in surveyor/user ID
                .actionDate(LocalDateTime.now())
                .remarks(
                        "Survey report submitted. Assessed damage amount: "
                                + request.getAssessedDamageAmount()
                                + ". Recommendation: "
                                + request.getRecommendation().trim()
                )
                .build();

        claimHistoryRepository.save(history);

        return surveyAssignmentMapper.toResponse(saved);
    }

    // 3. Get survey assignments for a claim
    @Override
    @Transactional(readOnly = true)
    public List<SurveyAssignmentResponse> getAssignmentsByClaimId(
            Long claimId) {

        if (!claimRepository.existsById(claimId)) {
            throw new RuntimeException(
                    "Claim not found: " + claimId);
        }

        return surveyRepository.findByClaim_ClaimId(claimId)
                .stream()
                .map(surveyAssignmentMapper::toResponse)
                .toList();
    }
}
