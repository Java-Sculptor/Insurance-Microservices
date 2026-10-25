package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.PaymentMode;
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
public class ReceiptResponseDto {
    private Long receiptId;
    private Long paymentId;
    private String receiptNumber;
    private Long policyId;
    private String policyNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private LocalDateTime receiptDate;
    private String receiptPath;
}
