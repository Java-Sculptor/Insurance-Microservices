package com.app.insurance_management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SurveyReportRequest {

    @NotBlank(message = "Damage description is required")
    private String damageDescription;

    @NotNull(message = "Assessed damage amount is required")
    @DecimalMin(value = "0.0", message = "Amount cannot be negative")
    private BigDecimal assessedDamageAmount;

    private String surveyorRemarks;

    @NotBlank(message = "Surveyor recommendation is required")
    private String recommendation;
}