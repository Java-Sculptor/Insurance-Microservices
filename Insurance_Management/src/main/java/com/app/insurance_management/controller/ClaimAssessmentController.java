package com.app.insurance_management.controller;

import com.app.insurance_management.dto.ClaimAssessmentResponse;
import com.app.insurance_management.serviceImpl.ClaimAssessmentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimAssessmentController {

    private final ClaimAssessmentServiceImpl claimAssessmentService;

    // Create Claim Assessment
    @PostMapping("/{claimId}/assessments")
    public ResponseEntity<ClaimAssessmentResponse> createAssessment(
            @PathVariable Long claimId) {

        ClaimAssessmentResponse response =
                claimAssessmentService.createAssessment(claimId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get Claim Assessment History
    @GetMapping("/{claimId}/assessments")
    public ResponseEntity<List<ClaimAssessmentResponse>> getAssessments(
            @PathVariable Long claimId) {

        List<ClaimAssessmentResponse> response =
                claimAssessmentService.getAssessmentsByClaimId(claimId);

        return ResponseEntity.ok(response);
    }
}
