package com.app.insurance_management.repository;

import com.app.insurance_management.entity.ClaimHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimHistoryRepository
        extends JpaRepository<ClaimHistory, Long> {

    List<ClaimHistory> findByClaim_ClaimIdOrderByActionDateDesc(
            Long claimId);
}