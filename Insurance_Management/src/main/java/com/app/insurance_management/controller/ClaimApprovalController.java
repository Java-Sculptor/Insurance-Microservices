package com.app.insurance_management.controller;

import com.app.insurance_management.dto.ClaimApprovalRequest;
import com.app.insurance_management.dto.ClaimApprovalResponse;
import com.app.insurance_management.service.ClaimApprovalService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimApprovalController {

    private final ClaimApprovalService claimApprovalService;

    public ClaimApprovalController(
            ClaimApprovalService claimApprovalService) {
        this.claimApprovalService = claimApprovalService;
    }

    @PutMapping("/{claimId}/approval")
    public ResponseEntity<ClaimApprovalResponse> approveOrRejectClaim(
            @PathVariable Long claimId,
            @Valid @RequestBody ClaimApprovalRequest request) {

        ClaimApprovalResponse response =
                claimApprovalService.approveOrRejectClaim(
                        claimId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClaimApprovalResponse>> getAllClaims()
    {
        List<ClaimApprovalResponse> responses=claimApprovalService.getAllClaims();

        return ResponseEntity.ok(responses);
    }
}
