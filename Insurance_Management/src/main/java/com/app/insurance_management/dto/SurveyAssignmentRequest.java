
package com.app.insurance_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SurveyAssignmentRequest {

    private Long surveyorId;
    private LocalDateTime inspectionDate;
}
