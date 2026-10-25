package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationDto {
    @NotBlank(message = "Transaction reference is mandatory")
    private String transactionReference;

    @NotNull(message = "Payment status is mandatory")
    private PaymentStatus paymentStatus;

    private String gatewayTransactionId;
    private String remarks;
}
