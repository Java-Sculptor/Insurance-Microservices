package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.dto.ClaimApprovalRequest;
import com.app.insurance_management.dto.ClaimApprovalResponse;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.mapper.ClaimMasterMapper;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.service.ClaimApprovalService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimApprovalServiceImpl
        implements ClaimApprovalService {

    private final ClaimMasterRepository claimMasterRepository;

    private final ClaimHistoryRepository claimHistoryRepository;



    @Override
    @Transactional
    public ClaimApprovalResponse approveOrRejectClaim(
            Long claimId,
            ClaimApprovalRequest request) {

        // 1. Find claim
        ClaimMaster claim = claimMasterRepository.findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found with ID: " + claimId));

        // 2. Validate request
        if (request == null || request.getApproved() == null) {
            throw new IllegalArgumentException(
                    "Approval decision is required");
        }

        // 3. Only claims awaiting approval can be processed
        if (claim.getClaimStatus() != ClaimStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Only claims with PENDING_APPROVAL status can be approved or rejected");
        }

        String action;
        String remarks;

        // 4. Approve or reject
        if (Boolean.TRUE.equals(request.getApproved())) {

            claim.setClaimStatus(ClaimStatus.APPROVED);

            action = "Claim Approved";
            remarks = "Claim has been approved successfully";

        } else {

            if (request.getReason() == null
                    || request.getReason().isBlank()) {
                throw new IllegalArgumentException(
                        "Rejection reason is required");
            }

            claim.setClaimStatus(ClaimStatus.REJECTED);
            claim.setRejectionReason(request.getReason().trim());

            action = "Claim Rejected";
            remarks = "Rejection reason: " + request.getReason().trim();
        }

        // 5. Save claim
        ClaimMaster savedClaim = claimMasterRepository.save(claim);

        // 6. Maintain claim history
        ClaimHistory history = ClaimHistory.builder()
                .claim(savedClaim)
                .action(action)
                .performedBy(1L)
                .actionDate(LocalDateTime.now())
                .remarks(remarks)
                .build();

        claimHistoryRepository.save(history);

        // 7. Build response
        ClaimApprovalResponse response = new ClaimApprovalResponse();
        response.setClaimId(savedClaim.getClaimId());
        response.setClaimNumber(savedClaim.getClaimNumber());
        response.setClaimStatus(savedClaim.getClaimStatus());
        response.setReason(savedClaim.getRejectionReason());

        return response;
    }


    @Override
    public List<ClaimApprovalResponse> getAllClaims()
    {
        return claimMasterRepository.findAll()
                .stream()
                .map(claim -> {
                            ClaimApprovalResponse response = new ClaimApprovalResponse();
                            response.setClaimId(claim.getClaimId());
                            response.setClaimStatus(claim.getClaimStatus());
                            response.setClaimNumber(claim.getClaimNumber());
                            response.setReason(claim.getRejectionReason());

                            return response;
                        })
                .toList();

    }
}
