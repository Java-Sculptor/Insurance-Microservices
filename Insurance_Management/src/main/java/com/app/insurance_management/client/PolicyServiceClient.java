package com.app.insurance_management.client;

import com.app.insurance_management.dto.PolicyEligibilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

/** Contract for claim eligibility checks owned by the Policy microservice. */
@FeignClient(name = "policy-service", url = "${policy.service.url}")
public interface PolicyServiceClient {
    @GetMapping("/api/v1/policies/{policyId}/claim-eligibility")
    PolicyEligibilityResponse getEligibility(@PathVariable("policyId") Long policyId,
        @RequestParam("incidentDate") LocalDate incidentDate,
        @RequestParam("claimType") String claimType);
}
