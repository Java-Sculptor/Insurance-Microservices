
package com.app.insurance_management.dto;

import com.app.insurance_management.enums.SurveyStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SurveyAssignmentResponse {

    private Long surveyId;
    private Long claimId;
    private Long surveyorId;
    private LocalDateTime assignedDate;
    private LocalDateTime inspectionDate;
    private String reportPath;
    private SurveyStatus surveyStatus;
}
