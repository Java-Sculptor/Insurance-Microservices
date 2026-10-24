package com.app.insurance_management.controller;

import com.app.insurance_management.apiResponse.ApiResponse;
import com.app.insurance_management.dto.InvestigationRequest;
import com.app.insurance_management.dto.InvestigationResponse;
import com.app.insurance_management.service.InvestigationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** REST endpoints for assigning investigations and submitting investigation reports. */
@RestController
@RequestMapping("/api/v1/claims/{claimId}/investigations")
@RequiredArgsConstructor
public class InvestigationController {
    private static final Logger log = LoggerFactory.getLogger(InvestigationController.class);
    private final InvestigationService investigationService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestigationResponse>> start(@PathVariable Long claimId, @Valid @RequestBody InvestigationRequest request) {
        log.info("Request to start investigation for claimId={}", claimId);
        InvestigationResponse result = investigationService.startInvestigation(claimId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<InvestigationResponse>builder().success(true).message("Investigation started successfully").data(result).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestigationResponse>>> getByClaim(@PathVariable Long claimId) {
        log.info("Fetching investigations for claimId={}", claimId);
        return ResponseEntity.ok(ApiResponse.<List<InvestigationResponse>>builder().success(true).message("Investigations fetched successfully").data(investigationService.getByClaim(claimId)).build());
    }

    @PatchMapping("/{investigationId}/report")
    public ResponseEntity<ApiResponse<InvestigationResponse>> submitReport(@PathVariable Long claimId, @PathVariable Long investigationId, @RequestBody Map<String, String> request) {
        log.info("Submitting report for investigationId={}, claimId={}", investigationId, claimId);
        InvestigationResponse result = investigationService.updateReport(claimId, investigationId, request.get("reportPath"));
        return ResponseEntity.ok(ApiResponse.<InvestigationResponse>builder().success(true).message("Investigation report submitted successfully").data(result).build());
    }
}
