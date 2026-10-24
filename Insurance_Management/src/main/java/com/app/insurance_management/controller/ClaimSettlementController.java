
package com.app.insurance_management.controller;

import com.app.insurance_management.dto.SettlementRequestDto;
import com.app.insurance_management.dto.SettlementResponseDto;
import com.app.insurance_management.service.ClaimSettlementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimSettlementController {

    private final ClaimSettlementService settlementService;

    // Create settlement and generate settlement advice PDF
    @PostMapping("/{claimId}/settlement")
    public ResponseEntity<SettlementResponseDto> createSettlement(
            @PathVariable Long claimId,
            @Valid @RequestBody SettlementRequestDto request) {

        SettlementResponseDto response =
                settlementService.createSettlement(claimId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get settlement details for a claim
    @GetMapping("/{claimId}/settlement")
    public ResponseEntity<SettlementResponseDto> getSettlement(
            @PathVariable Long claimId) {

        SettlementResponseDto response =
                settlementService.getSettlementByClaimId(claimId);

        return ResponseEntity.ok(response);
    }
}
