package com.ampta.premiumservice.service.impl;

import com.ampta.premiumservice.client.PolicyClient;
import com.ampta.premiumservice.dto.*;
import com.ampta.premiumservice.entity.PaymentReceipt;
import com.ampta.premiumservice.entity.PremiumPayment;
import com.ampta.premiumservice.entity.PremiumSchedule;
import com.ampta.premiumservice.entity.enums.InstallmentStatus;
import com.ampta.premiumservice.entity.enums.PaymentStatus;
import com.ampta.premiumservice.exception.BusinessException;
import com.ampta.premiumservice.exception.DuplicatePaymentException;
import com.ampta.premiumservice.exception.ResourceNotFoundException;
import com.ampta.premiumservice.repository.PaymentReceiptRepository;
import com.ampta.premiumservice.repository.PremiumPaymentRepository;
import com.ampta.premiumservice.repository.PremiumScheduleRepository;
import com.ampta.premiumservice.service.PremiumPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PremiumPaymentServiceImpl implements PremiumPaymentService {

    private final PremiumPaymentRepository paymentRepository;
    private final PremiumScheduleRepository scheduleRepository;
    private final PaymentReceiptRepository receiptRepository;
    private final PolicyClient policyClient;

    @Override
    public PaymentResponseDto payPremium(PaymentRequestDto request) {
        String txnRef = request.getTransactionReference();
        if (txnRef == null || txnRef.trim().isEmpty()) {
            txnRef = "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        if (paymentRepository.existsByTransactionReference(txnRef)) {
            throw new DuplicatePaymentException("Payment already exists for txn ref: " + txnRef);
        }

        PremiumSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + request.getScheduleId()));

        if (schedule.getStatus() == InstallmentStatus.PAID) {
            throw new BusinessException("Installment is already paid");
        }

        PolicyDto policy = null;
        try {
            ApiResponse<PolicyDto> response = policyClient.getPolicy(schedule.getPolicyId());
            if (response != null && response.getData() != null) {
                policy = response.getData();
            }
        } catch (Exception e) {
            log.warn("Could not retrieve policy from policy client, proceeding with fallback: {}", e.getMessage());
        }

        if (policy != null && policy.getPolicyStatus() != null) {
            String status = policy.getPolicyStatus().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("PENDING_PAYMENT") && !status.equals("DRAFT")) {
                throw new BusinessException("Policy is in " + status + " status. Payments are not allowed.");
            }
        }

        Long customerId = request.getCustomerId();
        if (customerId == null && policy != null) {
            customerId = policy.getCustomerId();
        }

        PremiumPayment payment = new PremiumPayment();
        payment.setScheduleId(schedule.getScheduleId());
        payment.setPolicyId(schedule.getPolicyId());
        payment.setCustomerId(customerId);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setTransactionReference(txnRef);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());

        PremiumPayment savedPayment = paymentRepository.save(payment);

        // Update the installment schedule
        BigDecimal lateFee = schedule.getLateFee() != null ? schedule.getLateFee() : BigDecimal.ZERO;
        BigDecimal totalDue = schedule.getPremiumAmount().add(lateFee);

        if (request.getAmountPaid().compareTo(totalDue) >= 0) {
            schedule.setStatus(InstallmentStatus.PAID);
        } else {
            schedule.setStatus(InstallmentStatus.PARTIAL_PAID);
        }
        schedule.setPaidDate(LocalDate.now());
        scheduleRepository.save(schedule);

        // Generate digital payment receipt
        createReceiptForPayment(savedPayment, policy != null && policy.getPolicyNumber() != null ? policy.getPolicyNumber() : "POL-" + savedPayment.getPolicyId());

        // Coordinate state transition with policy-service: activate policy upon successful payment
        // Note: Policy Service only activates policies that are in PENDING_PAYMENT or DRAFT status.
        // If the policy is already ACTIVE (e.g. installment 2+), skip activation to avoid 409 conflict.
        if (policy == null || "PENDING_PAYMENT".equalsIgnoreCase(policy.getPolicyStatus()) || "DRAFT".equalsIgnoreCase(policy.getPolicyStatus())) {
            try {
                policyClient.activatePolicy(schedule.getPolicyId());
                log.info("Successfully activated policy ID: {} in policy-service via OpenFeign", schedule.getPolicyId());
            } catch (Exception e) {
                log.warn("Could not auto-activate policy ID: {} via OpenFeign: {}", schedule.getPolicyId(), e.getMessage());
            }
        } else {
            log.info("Policy ID: {} is already in {} status; activation call skipped.", schedule.getPolicyId(), policy.getPolicyStatus());
        }

        log.info("Payment successfully processed for scheduleId: {}, paymentId: {}", schedule.getScheduleId(), savedPayment.getPaymentId());
        return toPaymentResponseDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId) {
        PremiumPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return toPaymentResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentHistory(Long policyId) {
        return paymentRepository.findByPolicyIdOrderByPaymentDateDesc(policyId)
                .stream()
                .map(this::toPaymentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponseDto verifyPayment(PaymentVerificationDto request) {
        PremiumPayment payment = paymentRepository.findByTransactionReference(request.getTransactionReference())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for transaction reference: " + request.getTransactionReference()));

        PaymentStatus oldStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(request.getPaymentStatus());
        PremiumPayment updatedPayment = paymentRepository.save(payment);

        // If payment transitioned to SUCCESS, update schedule & generate receipt
        if (request.getPaymentStatus() == PaymentStatus.SUCCESS && oldStatus != PaymentStatus.SUCCESS) {
            scheduleRepository.findById(payment.getScheduleId()).ifPresent(schedule -> {
                schedule.setStatus(InstallmentStatus.PAID);
                schedule.setPaidDate(LocalDate.now());
                scheduleRepository.save(schedule);
            });
            createReceiptForPayment(updatedPayment, "POL-" + updatedPayment.getPolicyId());
        }

        log.info("Payment verified. TxnRef: {}, New Status: {}", request.getTransactionReference(), request.getPaymentStatus());
        return toPaymentResponseDto(updatedPayment);
    }

    @Override
    public ReceiptResponseDto getReceipt(Long paymentId) {
        PremiumPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        PaymentReceipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseGet(() -> createReceiptForPayment(payment, "POL-" + payment.getPolicyId()));

        return toReceiptResponseDto(receipt, payment);
    }

    @Override
    public ReceiptResponseDto regenerateReceipt(Long paymentId) {
        PremiumPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        PaymentReceipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseGet(() -> new PaymentReceipt());

        String receiptNum = "REC-" + LocalDate.now().getYear() + "-" + String.format("%06d", payment.getPaymentId());
        receipt.setPaymentId(payment.getPaymentId());
        receipt.setPolicyId(payment.getPolicyId());
        receipt.setCustomerId(payment.getCustomerId());
        receipt.setAmountPaid(payment.getAmountPaid());
        receipt.setReceiptNumber(receiptNum);
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setReceiptPath("/api/v1/receipts/download/" + receiptNum + ".pdf");

        PaymentReceipt saved = receiptRepository.save(receipt);
        log.info("Receipt regenerated successfully for paymentId: {}, receiptNumber: {}", paymentId, receiptNum);
        return toReceiptResponseDto(saved, payment);
    }

    private PaymentReceipt createReceiptForPayment(PremiumPayment payment, String policyNumber) {
        String receiptNumber = "REC-" + LocalDate.now().getYear() + "-" + String.format("%06d", payment.getPaymentId());
        PaymentReceipt receipt = PaymentReceipt.builder()
                .paymentId(payment.getPaymentId())
                .policyId(payment.getPolicyId())
                .customerId(payment.getCustomerId())
                .amountPaid(payment.getAmountPaid())
                .receiptNumber(receiptNumber)
                .receiptDate(LocalDateTime.now())
                .receiptPath("/api/v1/receipts/download/" + receiptNumber + ".pdf")
                .build();
        return receiptRepository.save(receipt);
    }

    private PaymentResponseDto toPaymentResponseDto(PremiumPayment p) {
        return new PaymentResponseDto(
                p.getPaymentId(),
                p.getScheduleId(),
                p.getPolicyId(),
                p.getCustomerId(),
                p.getAmountPaid(),
                p.getPaymentMode(),
                p.getTransactionReference(),
                p.getPaymentStatus(),
                p.getPaymentDate(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private ReceiptResponseDto toReceiptResponseDto(PaymentReceipt r, PremiumPayment p) {
        String policyNum = "POL-" + r.getPolicyId();
        try {
            ApiResponse<PolicyDto> polResp = policyClient.getPolicy(r.getPolicyId());
            if (polResp != null && polResp.getData() != null && polResp.getData().getPolicyNumber() != null) {
                policyNum = polResp.getData().getPolicyNumber();
            }
        } catch (Exception ignored) {
        }

        return ReceiptResponseDto.builder()
                .receiptId(r.getReceiptId())
                .paymentId(r.getPaymentId())
                .receiptNumber(r.getReceiptNumber())
                .policyId(r.getPolicyId())
                .policyNumber(policyNum)
                .customerId(r.getCustomerId())
                .customerName("Customer #" + r.getCustomerId())
                .amountPaid(r.getAmountPaid())
                .paymentMode(p != null ? p.getPaymentMode() : null)
                .transactionReference(p != null ? p.getTransactionReference() : null)
                .paymentDate(p != null ? p.getPaymentDate() : null)
                .receiptDate(r.getReceiptDate())
                .receiptPath(r.getReceiptPath())
                .build();
    }
}
