
package com.app.insurance_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClaimAssessmentResponse {

    private Long assessmentId;
    private Long claimId;
    private BigDecimal assessedAmount;
    private BigDecimal deductible;
    private BigDecimal depreciation;
    private BigDecimal nonPayable;
    private BigDecimal netPayable;
}
