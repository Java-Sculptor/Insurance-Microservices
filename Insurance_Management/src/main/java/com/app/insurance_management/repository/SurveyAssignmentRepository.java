
package com.app.insurance_management.repository;

import com.app.insurance_management.entity.SurveyAssignment;
import com.app.insurance_management.enums.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyAssignmentRepository
        extends JpaRepository<SurveyAssignment, Long> {

    List<SurveyAssignment> findByClaim_ClaimId(Long claimId);

    boolean existsByClaim_ClaimIdAndSurveyStatus(
            Long claimId,
            SurveyStatus surveyStatus
    );
}
