
package com.app.insurance_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClaimAssessmentRequest {

    private BigDecimal assessedAmount;
    private BigDecimal deductible;
    private BigDecimal depreciation;
    private BigDecimal nonPayable;
}
