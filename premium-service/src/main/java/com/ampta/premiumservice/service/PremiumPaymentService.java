package com.ampta.premiumservice.service;

import com.ampta.premiumservice.dto.*;

import java.util.List;

public interface PremiumPaymentService {
    PaymentResponseDto payPremium(PaymentRequestDto request);
    PaymentResponseDto getPayment(Long paymentId);
    List<PaymentResponseDto> getPaymentHistory(Long policyId);
    PaymentResponseDto verifyPayment(PaymentVerificationDto request);
    ReceiptResponseDto getReceipt(Long paymentId);
    ReceiptResponseDto regenerateReceipt(Long paymentId);
}
