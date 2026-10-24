package com.app.insurance_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestigationRequest {
    @NotNull(message = "Investigator ID is required")
    @Positive(message = "Investigator ID must be positive")
    private Long investigatorId;

    @NotBlank(message = "Investigation reason is required")
    private String investigationReason;
}
