package com.app.insurance_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimApprovalRequest {

    @NotNull(message = "Approval decision is required")
    private Boolean approved;

    private String reason;
}
