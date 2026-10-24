package com.app.insurance_management.repository;

import com.app.insurance_management.entity.ClaimAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimAssessmentRepository
        extends JpaRepository<ClaimAssessment, Long> {

    List<ClaimAssessment> findByClaim_ClaimId(Long claimId);
}