package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.PaymentMode;
import com.ampta.premiumservice.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private Long scheduleId;
    private Long policyId;
    private Long customerId;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private String transactionReference;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
