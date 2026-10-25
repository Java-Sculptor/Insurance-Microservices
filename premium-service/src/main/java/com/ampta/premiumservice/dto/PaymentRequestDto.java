package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    @NotNull(message = "Schedule ID is mandatory")
    private Long scheduleId;

    @NotNull(message = "Policy ID is mandatory")
    private Long policyId;

    @NotNull(message = "Customer ID is mandatory")
    private Long customerId;

    @NotNull(message = "Amount paid is mandatory")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is mandatory")
    private PaymentMode paymentMode;

    private String transactionReference;

    private LocalDateTime paymentDate;
}
