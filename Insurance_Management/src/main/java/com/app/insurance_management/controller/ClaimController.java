package com.app.insurance_management.controller;

import com.app.insurance_management.apiResponse.ApiResponse;
import com.app.insurance_management.dto.ClaimRequest;
import com.app.insurance_management.dto.ClaimResponse;
import com.app.insurance_management.service.ClaimService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claim")
@RequiredArgsConstructor
public class ClaimController {

    private static final Logger log = LoggerFactory.getLogger(ClaimController.class);
    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClaimResponse>> registerClaim(@Valid @RequestBody ClaimRequest claimRequest)
    {
        log.info("Received claim registration request for policyId={}, customerId={}", claimRequest.getPolicyId(), claimRequest.getCustomerId());
        ClaimResponse claimResponse= claimService.claimRegister(claimRequest);

        ApiResponse<ClaimResponse> apiResponse=ApiResponse.<ClaimResponse>builder()
                .success(true)
                .message("Claim Register Successfully")
                .data(claimResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getAllClaims()
    {

        log.info("Received request to fetch all claims");
        List<ClaimResponse> claimResponseList=claimService.getAllClaims();

        ApiResponse<List<ClaimResponse>> apiResponse=ApiResponse.<List<ClaimResponse>>builder()
                .success(true)
                .data(claimResponseList)
                .message("Fetch all Claims")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaimDetails(@PathVariable Long claimId)
    {
        log.info("Received request to fetch claimId={}", claimId);
        ClaimResponse claimResponse=claimService.getClaimDetails(claimId);

        ApiResponse<ClaimResponse> apiResponse=ApiResponse.<ClaimResponse>builder()
                .success(true)
                .data(claimResponse)
                .message("Successfully fetch the Claim Details")
                .build();

        return ResponseEntity.ok(apiResponse);

    }
}
