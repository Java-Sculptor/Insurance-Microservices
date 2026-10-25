package com.ampta.premiumservice.controller;

import com.ampta.premiumservice.dto.ApiResponse;
import com.ampta.premiumservice.dto.RefundApprovalDto;
import com.ampta.premiumservice.dto.RefundRequestDto;
import com.ampta.premiumservice.dto.RefundResponseDto;
import com.ampta.premiumservice.service.PremiumRefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refunds")
@Slf4j
public class RefundController {

    private final PremiumRefundService refundService;

    @PostMapping
    public ResponseEntity<ApiResponse<RefundResponseDto>> createRefund(@RequestBody @Valid RefundRequestDto request) {
        log.info("REST request to initiate refund for payment ID: {}", request.getPaymentId());
        RefundResponseDto response = refundService.createRefund(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Refund request initiated successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{refundId}/approve")
    public ResponseEntity<ApiResponse<RefundResponseDto>> approveRefund(
            @PathVariable Long refundId,
            @RequestBody @Valid RefundApprovalDto approvalDto) {
        log.info("REST request to process approval for refund ID: {}", refundId);
        RefundResponseDto response = refundService.approveRefund(refundId, approvalDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Refund status updated successfully", response));
    }

    @GetMapping("/{refundId}")
    public ResponseEntity<ApiResponse<RefundResponseDto>> getRefund(@PathVariable Long refundId) {
        log.info("REST request to get refund details for refund ID: {}", refundId);
        RefundResponseDto response = refundService.getRefund(refundId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Refund details retrieved successfully", response));
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<ApiResponse<List<RefundResponseDto>>> getRefundsByPolicy(@PathVariable Long policyId) {
        log.info("REST request to get refunds for policy ID: {}", policyId);
        List<RefundResponseDto> response = refundService.getRefundsByPolicy(policyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy refunds retrieved successfully", response));
    }
}
