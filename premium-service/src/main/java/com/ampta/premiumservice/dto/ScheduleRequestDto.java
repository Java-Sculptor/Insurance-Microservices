package com.ampta.premiumservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestDto {
    private Long policyId;
    private String premiumFrequency;
    private Integer policyTermMonths;
    private BigDecimal finalPremium;

    private LocalDate startDate;
    private Long customerId;
}
