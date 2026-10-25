package com.ampta.premiumservice.service.impl;

import com.ampta.premiumservice.client.PolicyClient;
import com.ampta.premiumservice.dto.ApiResponse;
import com.ampta.premiumservice.dto.PolicyDto;
import com.ampta.premiumservice.dto.RefundApprovalDto;
import com.ampta.premiumservice.dto.RefundRequestDto;
import com.ampta.premiumservice.dto.RefundResponseDto;
import com.ampta.premiumservice.entity.PremiumPayment;
import com.ampta.premiumservice.entity.PremiumRefund;
import com.ampta.premiumservice.entity.enums.PaymentStatus;
import com.ampta.premiumservice.entity.enums.RefundStatus;
import com.ampta.premiumservice.exception.BusinessException;
import com.ampta.premiumservice.exception.ResourceNotFoundException;
import com.ampta.premiumservice.repository.PremiumPaymentRepository;
import com.ampta.premiumservice.repository.PremiumRefundRepository;
import com.ampta.premiumservice.service.PremiumRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PremiumRefundServiceImpl implements PremiumRefundService {

    private final PremiumRefundRepository refundRepository;
    private final PremiumPaymentRepository paymentRepository;
    private final PolicyClient policyClient;

    @Override
    public RefundResponseDto createRefund(RefundRequestDto request) {
        PremiumPayment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + request.getPaymentId()));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Refund can only be initiated for successful payments");
        }

        // Verify policy information via Feign Client
        try {
            ApiResponse<PolicyDto> polResp = policyClient.getPolicy(payment.getPolicyId());
            if (polResp != null && polResp.getData() != null) {
                log.info("Processing refund for policy ID: {}, number: {}, status: {}",
                        polResp.getData().getPolicyId(), polResp.getData().getPolicyNumber(), polResp.getData().getPolicyStatus());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve policy from policy client for refund verification: {}", e.getMessage());
        }

        if (request.getRefundAmount().compareTo(payment.getAmountPaid()) > 0) {
            throw new BusinessException("Refund amount cannot exceed original amount paid: " + payment.getAmountPaid());
        }

        PremiumRefund refund = PremiumRefund.builder()
                .paymentId(payment.getPaymentId())
                .policyId(payment.getPolicyId())
                .refundAmount(request.getRefundAmount())
                .refundStatus(RefundStatus.PENDING)
                .refundDate(LocalDateTime.now())
                .reason(request.getReason())
                .build();

        PremiumRefund saved = refundRepository.save(refund);
        log.info("Refund initiated successfully with ID: {} for paymentId: {}", saved.getRefundId(), payment.getPaymentId());
        return toDto(saved);
    }

    @Override
    public RefundResponseDto approveRefund(Long refundId, RefundApprovalDto approvalDto) {
        PremiumRefund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with ID: " + refundId));

        if (refund.getRefundStatus() != RefundStatus.PENDING) {
            throw new BusinessException("Refund has already been processed with status: " + refund.getRefundStatus());
        }

        refund.setRefundStatus(approvalDto.getRefundStatus());
        refund.setApprovedBy(approvalDto.getApprovedBy());
        refund.setRefundDate(LocalDateTime.now());

        if (approvalDto.getRefundStatus() == RefundStatus.APPROVED || approvalDto.getRefundStatus() == RefundStatus.COMPLETED) {
            paymentRepository.findById(refund.getPaymentId()).ifPresent(payment -> {
                payment.setPaymentStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            });
        }

        PremiumRefund updated = refundRepository.save(refund);
        log.info("Refund ID: {} status updated to: {} by employee: {}", refundId, approvalDto.getRefundStatus(), approvalDto.getApprovedBy());
        return toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponseDto getRefund(Long refundId) {
        PremiumRefund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found with ID: " + refundId));
        return toDto(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponseDto> getRefundsByPolicy(Long policyId) {
        return refundRepository.findByPolicyId(policyId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RefundResponseDto toDto(PremiumRefund r) {
        return RefundResponseDto.builder()
                .refundId(r.getRefundId())
                .paymentId(r.getPaymentId())
                .policyId(r.getPolicyId())
                .refundAmount(r.getRefundAmount())
                .refundDate(r.getRefundDate())
                .refundStatus(r.getRefundStatus())
                .approvedBy(r.getApprovedBy())
                .reason(r.getReason())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
