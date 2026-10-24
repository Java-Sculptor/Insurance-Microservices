
package com.app.insurance_management.mapper;

import com.app.insurance_management.dto.SurveyAssignmentRequest;
import com.app.insurance_management.dto.SurveyAssignmentResponse;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.SurveyAssignment;
import com.app.insurance_management.enums.SurveyStatus;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SurveyAssignmentMapper {

    // Convert request data into a SurveyAssignment entity
    public SurveyAssignment toEntity(
            ClaimMaster claim,
            SurveyAssignmentRequest request) {

        SurveyAssignment assignment = new SurveyAssignment();

        assignment.setClaim(claim);
        assignment.setSurveyorId(request.getSurveyorId());
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setInspectionDate(request.getInspectionDate());
        assignment.setSurveyStatus(SurveyStatus.ASSIGNED);

        return assignment;
    }

    // Convert entity into a response DTO
    public SurveyAssignmentResponse toResponse(
            SurveyAssignment assignment) {

        SurveyAssignmentResponse response =
                new SurveyAssignmentResponse();

        response.setSurveyId(assignment.getSurveyId());
        response.setClaimId(
                assignment.getClaim().getClaimId());
        response.setSurveyorId(assignment.getSurveyorId());
        response.setAssignedDate(assignment.getAssignedDate());
        response.setInspectionDate(assignment.getInspectionDate());
        response.setReportPath(assignment.getReportPath());
        response.setSurveyStatus(assignment.getSurveyStatus());

        return response;
    }
}
