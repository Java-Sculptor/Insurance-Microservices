package com.ampta.premiumservice.controller;

import com.ampta.premiumservice.dto.*;
import com.ampta.premiumservice.service.PremiumPaymentService;
import com.ampta.premiumservice.service.PremiumScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class PremiumController {

    private final PremiumScheduleService premiumScheduleService;
    private final PremiumPaymentService premiumPaymentService;

    // --- Policy Service Feign Integration APIs ---

    @GetMapping("/policies/{policyId}/policy-details")
    public ResponseEntity<ApiResponse<PolicyDto>> getPolicyDetails(@PathVariable Long policyId) {
        log.info("REST request to get policy details from Policy Service via OpenFeign for policy ID: {}", policyId);
        PolicyDto response = premiumScheduleService.getPolicyDetails(policyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy details fetched successfully from Policy Service", response));
    }

    // --- Premium Schedule APIs ---

    @PostMapping("/premium-schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> generate(@RequestBody @Valid ScheduleRequestDto request) {
        log.info("REST request to generate premium schedules for policy ID: {}", request.getPolicyId());
        List<ScheduleResponseDto> response = premiumScheduleService.generateSchedule(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Premium schedules generated successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/premium-schedules/generate-from-policy/{policyId}")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> generateFromPolicy(
            @PathVariable Long policyId,
            @RequestParam(required = false, defaultValue = "YEARLY") String frequency,
            @RequestParam(required = false) BigDecimal finalPremium) {
        log.info("REST request to generate premium schedules directly from policy ID: {}", policyId);
        List<ScheduleResponseDto> response = premiumScheduleService.generateScheduleFromPolicy(policyId, frequency, finalPremium);
        return new ResponseEntity<>(new ApiResponse<>(true, "Premium schedules generated from Policy Service successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/premium-schedules/{policyId}")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDto>>> getScheduleByPolicyId(@PathVariable Long policyId) {
        log.info("REST request to get premium schedules for policy ID: {}", policyId);
        List<ScheduleResponseDto> response = premiumScheduleService.getScheduleByPolicy(policyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Premium schedules retrieved successfully", response));
    }

    @PutMapping("/premium-schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponseDto>> update(@PathVariable Long scheduleId, @RequestBody ScheduleUpdateDto dto) {
        log.info("REST request to update installment schedule ID: {}", scheduleId);
        ScheduleResponseDto response = premiumScheduleService.updateInstallment(scheduleId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Installment schedule updated successfully", response));
    }

    @PostMapping("/premium-schedules/trigger-overdue-assessment")
    public ResponseEntity<ApiResponse<String>> triggerOverdueAssessment() {
        log.info("REST request to manually trigger overdue & late fee assessment scheduler");
        int updated = premiumScheduleService.assessOverdueAndCalculateLateFees();
        return ResponseEntity.ok(new ApiResponse<>(true, "Overdue assessment completed. Updated records: " + updated, null));
    }

    // --- Premium Payment APIs ---

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> pay(@RequestBody @Valid PaymentRequestDto dto) {
        log.info("REST request to process premium payment for schedule ID: {}", dto.getScheduleId());
        PaymentResponseDto response = premiumPaymentService.payPremium(dto);
        return new ResponseEntity<>(new ApiResponse<>(true, "Premium payment processed successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(@PathVariable Long paymentId) {
        log.info("REST request to get payment details for payment ID: {}", paymentId);
        PaymentResponseDto response = premiumPaymentService.getPayment(paymentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment details retrieved successfully", response));
    }

    @GetMapping("/payments/history/{policyId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> history(@PathVariable Long policyId) {
        log.info("REST request to get payment history for policy ID: {}", policyId);
        List<PaymentResponseDto> response = premiumPaymentService.getPaymentHistory(policyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment history retrieved successfully", response));
    }

    @PostMapping("/payments/verify")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> verifyPayment(@RequestBody @Valid PaymentVerificationDto dto) {
        log.info("REST request to verify payment transaction ref: {}", dto.getTransactionReference());
        PaymentResponseDto response = premiumPaymentService.verifyPayment(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment verification updated successfully", response));
    }

    // --- Receipt APIs ---

    @GetMapping("/payments/{paymentId}/receipt")
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> getReceipt(@PathVariable Long paymentId) {
        log.info("REST request to get payment receipt for payment ID: {}", paymentId);
        ReceiptResponseDto response = premiumPaymentService.getReceipt(paymentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Receipt retrieved successfully", response));
    }

    @PostMapping("/payments/{paymentId}/receipt/regenerate")
    public ResponseEntity<ApiResponse<ReceiptResponseDto>> regenerateReceipt(@PathVariable Long paymentId) {
        log.info("REST request to regenerate receipt for payment ID: {}", paymentId);
        ReceiptResponseDto response = premiumPaymentService.regenerateReceipt(paymentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Receipt regenerated successfully", response));
    }
}
