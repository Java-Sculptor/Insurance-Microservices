package com.ampta.premiumservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequestDto {
    @NotNull(message = "Payment ID is mandatory")
    private Long paymentId;

    @NotNull(message = "Refund amount is mandatory")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than zero")
    private BigDecimal refundAmount;

    private String reason;
}
