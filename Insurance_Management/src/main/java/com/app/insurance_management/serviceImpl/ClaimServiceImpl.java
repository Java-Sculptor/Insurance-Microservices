
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.dto.ClaimRequest;
import com.app.insurance_management.dto.ClaimResponse;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.mapper.ClaimMasterMapper;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.service.ClaimService;
import com.app.insurance_management.exception.BusinessRuleException;
import com.app.insurance_management.exception.ClaimNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);
    private final ClaimEligibilityService claimEligibilityService;

    private final ClaimMasterRepository claimMasterRepository;

    private final ClaimMasterMapper claimMasterMapper;

    private final ClaimHistoryRepository claimHistoryRepository;

    // 1. Register a new claim
    @Override
    @Transactional
    public ClaimResponse claimRegister(ClaimRequest claimRequest) {

        // Verify policy, customer ownership, premium schedule, coverage and claim limit before persistence.
        var eligibility = claimEligibilityService.validate(
                claimRequest.getPolicyId(), claimRequest.getCustomerId(),
                claimRequest.getIncidentDate(), claimRequest.getClaimType().name(),
                claimRequest.getEstimatedLoss());
        if (!eligibility.eligible()) {
            throw new BusinessRuleException(String.join("; ", eligibility.errors()));
        }
        ClaimMaster claim = claimMasterMapper.toEntity(claimRequest);
        ClaimMaster savedClaim = claimMasterRepository.save(claim);
        log.info("Claim registered successfully: claimId={}, claimNumber={}, customerId={}, policyId={}", savedClaim.getClaimId(), savedClaim.getClaimNumber(), savedClaim.getCustomerId(), savedClaim.getPolicyId());

        return claimMasterMapper.toResponse(savedClaim);
    }

    // 2. Get all claims
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {

        log.info("Fetching all claims");
        return claimMasterRepository.findAll()
                .stream()
                .map(claimMasterMapper::toResponse)
                .toList();
    }

    // 3. Get claim details by ID
    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimDetails(Long claimId) {

        ClaimMaster claim = claimMasterRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found with ID: " + claimId));
        log.info("Fetched claim details: claimId={}", claimId);

        return claimMasterMapper.toResponse(claim);
    }
}
