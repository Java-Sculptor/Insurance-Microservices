package com.app.insurance_management.repository;

import com.app.insurance_management.entity.Investigation;
import com.app.insurance_management.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationRepository
        extends JpaRepository<Investigation, Long> {

    List<Investigation> findByClaim_ClaimId(Long claimId);

    List<Investigation> findByInvestigatorId(Long investigatorId);

    List<Investigation> findByInvestigationStatus(ClaimStatus investigationStatus);
}