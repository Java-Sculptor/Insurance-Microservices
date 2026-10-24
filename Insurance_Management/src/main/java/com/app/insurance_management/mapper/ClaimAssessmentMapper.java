package com.app.insurance_management.mapper;

import com.app.insurance_management.dto.ClaimAssessmentResponse;
import com.app.insurance_management.entity.ClaimAssessment;
import com.app.insurance_management.entity.ClaimMaster;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClaimAssessmentMapper {

    public ClaimAssessment toEntity(
            ClaimMaster claimMaster,
            BigDecimal assessedAmount,
            BigDecimal deductible,
            BigDecimal depreciation,
            BigDecimal nonPayable,
            BigDecimal netPayable) {

        ClaimAssessment assessment = new ClaimAssessment();

        assessment.setClaim(claimMaster);
        assessment.setAssessedAmount(assessedAmount);
        assessment.setDeductible(deductible);
        assessment.setDepreciation(depreciation);
        assessment.setNonPayable(nonPayable);
        assessment.setNetPayable(netPayable);

        return assessment;
    }

    public ClaimAssessmentResponse toResponse(
            ClaimAssessment assessment) {

        ClaimAssessmentResponse response =
                new ClaimAssessmentResponse();

        response.setAssessmentId(assessment.getAssessmentId());
        response.setClaimId(assessment.getClaim().getClaimId());
        response.setAssessedAmount(assessment.getAssessedAmount());
        response.setDeductible(assessment.getDeductible());
        response.setDepreciation(assessment.getDepreciation());
        response.setNonPayable(assessment.getNonPayable());
        response.setNetPayable(assessment.getNetPayable());

        return response;
    }
}