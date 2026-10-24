
package com.app.insurance_management.service;

import com.app.insurance_management.dto.SurveyAssignmentRequest;
import com.app.insurance_management.dto.SurveyAssignmentResponse;
import com.app.insurance_management.dto.SurveyReportRequest;

import java.util.List;

public interface SurveyAssignmentService {

    SurveyAssignmentResponse assignSurveyor(
            Long claimId,
            SurveyAssignmentRequest request
    );

    SurveyAssignmentResponse submitSurveyReport(
            Long claimId,
            Long surveyId,
            SurveyReportRequest request
    );

    List<SurveyAssignmentResponse> getAssignmentsByClaimId(
            Long claimId
    );
}
