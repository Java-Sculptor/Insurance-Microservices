package com.app.insurance_management.repository;

import com.app.insurance_management.entity.ClaimDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimDocumentRepository
        extends JpaRepository<ClaimDocument, Long> {

    List<ClaimDocument> findByClaim_ClaimId(Long claimId);

    boolean existsByClaim_ClaimId(Long claimId);

    List<ClaimDocument> findByVerificationStatus(
            String verificationStatus);
}