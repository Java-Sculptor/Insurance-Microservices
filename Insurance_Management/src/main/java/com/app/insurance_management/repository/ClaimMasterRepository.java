package com.app.insurance_management.repository;

import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimMasterRepository
        extends JpaRepository<ClaimMaster, Long> {

    Optional<ClaimMaster> findByClaimNumber(String claimNumber);

    boolean existsByClaimNumber(String claimNumber);

    List<ClaimMaster> findByCustomerId(Long customerId);

    List<ClaimMaster> findByPolicyId(Long policyId);

    List<ClaimMaster> findByClaimStatus(ClaimStatus claimStatus);
}