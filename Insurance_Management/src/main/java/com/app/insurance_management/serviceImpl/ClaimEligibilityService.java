package com.app.insurance_management.serviceImpl;


import com.app.insurance_management.dto.ClaimEligibilityResponse;
import com.app.insurance_management.dto.PolicyEligibilityResponse;
import com.app.insurance_management.dto.PremiumEligibilityResponse;
import com.app.insurance_management.client.PolicyServiceClient;
import com.app.insurance_management.client.PremiumServiceClient;
import com.app.insurance_management.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClaimEligibilityService {

    private final PolicyServiceClient policyClient;
    private final PremiumServiceClient premiumClient;

//    @Value("${claim.external-services.mock-enabled:false}")
//    private boolean mockEnabled;


    public ClaimEligibilityService(
            PolicyServiceClient policyClient,
            PremiumServiceClient premiumClient) {
        this.policyClient = policyClient;
        this.premiumClient = premiumClient;
    }

    public ClaimEligibilityResponse validate(
            Long policyId,
            Long customerId,
            LocalDate incidentDate,
            String claimType,
            BigDecimal estimatedLoss) {

        List<String> errors = new ArrayList<>();

        // Claim business rule: incident date cannot be in future
        if (incidentDate == null || incidentDate.isAfter(LocalDate.now())) {
            errors.add("Incident date cannot be in the future");
            return new ClaimEligibilityResponse(false, errors);
        }

        if (estimatedLoss == null
                || estimatedLoss.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Estimated loss must be greater than zero");
            return new ClaimEligibilityResponse(false, errors);
        }

        // ************************************************************************
//        // Temporary testing mode: bypass external Policy/Premium calls.
//        if (mockEnabled) {
//            List<String> mockErrors = new ArrayList<>();
//
//            // Mock policy/customer details for local testing only.
//            if (policyId == null || policyId <= 0) {
//                mockErrors.add("Invalid policy ID");
//            }
//
//            if (customerId == null || customerId <= 0) {
//                mockErrors.add("Invalid customer ID");
//            }
//
//            // Example mock claim limit: Rs. 5,00,000
//            BigDecimal mockRemainingClaimLimit = new BigDecimal("500000");
//
//            if (estimatedLoss.compareTo(mockRemainingClaimLimit) > 0) {
//                mockErrors.add("Estimated loss exceeds the mock claim limit");
//            }
//
//            return new ClaimEligibilityResponse(
//                    mockErrors.isEmpty(),
//                    mockErrors
//            );
//        }

        // ************************************************************************************

        // Call Policy microservice
        PolicyEligibilityResponse policy;
        PremiumEligibilityResponse premium;

        try {
            policy = policyClient.getEligibility(policyId, incidentDate, claimType);
            premium = premiumClient.getEligibility(policyId);
        } catch (Exception ex) {
            throw new ExternalServiceException("Policy or Premium service is unavailable; claim eligibility cannot be verified", ex);
        }
        if (policy == null) throw new ExternalServiceException("Policy service returned an empty eligibility response");
        if (premium == null) throw new ExternalServiceException("Premium service returned an empty eligibility response");

        // Ensure the customer submitting the claim owns the policy.
        if (policy.customerId() == null || !policy.customerId().equals(customerId)) {
            errors.add("Customer ID does not match the customer linked to this policy");
        }

        // 1. Policy status
        if (!"Active".equalsIgnoreCase(policy.policyStatus())) {
            errors.add("Policy is not Active");
        }

        // 2. Policy validity on incident date
        if (policy.effectiveDate() == null
                || incidentDate.isBefore(policy.effectiveDate())) {
            errors.add("Policy was not effective on the incident date");
        }

        if (policy.expiryDate() == null
                || incidentDate.isAfter(policy.expiryDate())) {
            errors.add("Policy was expired on the incident date");
        }

        // 3. Premium status
        if (!premium.premiumUpToDate()) {
            errors.add("Premium payments are not up to date");
        }

        // 4. Coverage
        if (!policy.coverageAvailable()) {
            errors.add("Required coverage is not available");
        }

        // 5. Waiting period
        if (!policy.waitingPeriodCompleted()) {
            errors.add("Policy waiting period is not completed");
        }

        // 6. Claim limit
        if (policy.remainingClaimLimit() == null
                || policy.remainingClaimLimit().compareTo(estimatedLoss) < 0) {
            errors.add("Estimated loss exceeds the remaining claim limit");
        }



        return new ClaimEligibilityResponse(errors.isEmpty(), errors);
    }
}