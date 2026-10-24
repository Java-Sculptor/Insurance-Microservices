
package com.app.insurance_management.repository;

import com.app.insurance_management.entity.ClaimSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimSettlementRepository
        extends JpaRepository<ClaimSettlement, Long> {

    Optional<ClaimSettlement> findByClaim_ClaimId(Long claimId);

    boolean existsByClaim_ClaimId(Long claimId);
}
