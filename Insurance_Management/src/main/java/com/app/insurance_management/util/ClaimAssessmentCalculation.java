package com.app.insurance_management.util;

import java.math.BigDecimal;

public final class ClaimAssessmentCalculation {

    private ClaimAssessmentCalculation() {
    }

    public static BigDecimal calculateNetPayable(
            BigDecimal assessedAmount,
            BigDecimal deductible,
            BigDecimal depreciation,
            BigDecimal nonPayable) {

        if (assessedAmount == null || deductible == null
                || depreciation == null || nonPayable == null) {
            throw new IllegalArgumentException(
                    "Assessment amounts cannot be null");
        }

        if (assessedAmount.signum() <= 0
                || deductible.signum() < 0
                || depreciation.signum() < 0
                || nonPayable.signum() < 0) {
            throw new IllegalArgumentException(
                    "Invalid assessment amounts");
        }

        BigDecimal netPayable = assessedAmount
                .subtract(deductible)
                .subtract(depreciation)
                .subtract(nonPayable);

        if (netPayable.signum() < 0) {
            throw new IllegalArgumentException(
                    "Deductions cannot exceed assessed amount");
        }

        return netPayable;
    }
}