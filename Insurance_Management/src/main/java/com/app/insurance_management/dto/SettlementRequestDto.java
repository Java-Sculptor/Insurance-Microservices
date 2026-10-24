package com.app.insurance_management.dto;

import com.app.insurance_management.enums.PaymentMode;
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
public class SettlementRequestDto {

    @NotNull(message = "Payment mode is mandatory")
    private PaymentMode paymentMode;

    private String transactionReference;

    @NotBlank(message = "Bank account number is mandatory")
    private String bankAccountNumber;

    @NotBlank(message = "Bank IFSC code is mandatory")
    private String bankIfscCode;

    private String remarks;
}