package com.app.insurance_management.controller;

import com.app.insurance_management.dto.SurveyAssignmentRequest;
import com.app.insurance_management.dto.SurveyAssignmentResponse;
import com.app.insurance_management.dto.SurveyReportRequest;
import com.app.insurance_management.serviceImpl.SurveyAssignmentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims/{claimId}/surveys")
@RequiredArgsConstructor
public class SurveyAssignmentController {

    public final SurveyAssignmentServiceImpl surveyAssignmentService;

    @PostMapping
    public ResponseEntity<SurveyAssignmentResponse> assignSurveyor(@PathVariable Long claimId,
                                                                   @RequestBody SurveyAssignmentRequest request)
    {
        SurveyAssignmentResponse response= surveyAssignmentService.assignSurveyor(claimId,request);

        return ResponseEntity.ok(response);
    }

    // 2. Submit survey findings and generate PDF automatically
    @PutMapping("/{surveyId}/report")
    public ResponseEntity<SurveyAssignmentResponse> submitSurveyReport(
            @PathVariable Long claimId,
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyReportRequest request) {

        SurveyAssignmentResponse response =
                surveyAssignmentService.submitSurveyReport(
                        claimId,
                        surveyId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // 3. Get all survey assignments for a claim
    @GetMapping
    public ResponseEntity<List<SurveyAssignmentResponse>> getAssignments(
            @PathVariable Long claimId) {

        List<SurveyAssignmentResponse> response =
                surveyAssignmentService.getAssignmentsByClaimId(claimId);

        return ResponseEntity.ok(response);
    }

}
