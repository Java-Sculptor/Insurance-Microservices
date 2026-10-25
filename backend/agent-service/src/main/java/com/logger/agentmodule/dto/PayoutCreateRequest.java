package com.logger.agentmodule.dto;
import jakarta.validation.constraints.NotBlank;

public class PayoutCreateRequest {

    @NotBlank
    private String paymentMode;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}

