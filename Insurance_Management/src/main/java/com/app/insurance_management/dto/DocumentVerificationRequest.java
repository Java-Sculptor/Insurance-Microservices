package com.app.insurance_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentVerificationRequest {

    @NotBlank(message = "Verification status is required")
    private String verificationStatus;

    private String remarks;
}