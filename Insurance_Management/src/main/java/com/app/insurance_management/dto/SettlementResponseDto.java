
package com.app.insurance_management.dto;

import com.app.insurance_management.enums.PaymentMode;
import com.app.insurance_management.enums.SettlementStatus;
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
public class SettlementResponseDto {

    private Long settlementId;

    private Long claimId;

    private String claimNumber;

    private BigDecimal approvedAmount;

    private PaymentMode paymentMode;

    private String transactionReference;

    private LocalDateTime settlementDate;

    private SettlementStatus settlementStatus;

    private String settlementAdvicePath;

    private String remarks;
}
