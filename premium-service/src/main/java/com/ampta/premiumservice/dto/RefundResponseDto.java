package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundResponseDto {
    private Long refundId;
    private Long paymentId;
    private Long policyId;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private RefundStatus refundStatus;
    private Long approvedBy;
    private String reason;
    private LocalDateTime createdAt;
}
