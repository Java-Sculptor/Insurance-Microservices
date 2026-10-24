package com.app.insurance_management.client;

import com.app.insurance_management.dto.PremiumEligibilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Contract for payment and premium eligibility checks owned by Premium Schedule service. */
@FeignClient(name = "premium-service", url = "${premium.service.url}")
public interface PremiumServiceClient {
    @GetMapping("/api/v1/premiums/policy/{policyId}/claim-eligibility")
    PremiumEligibilityResponse getEligibility(@PathVariable("policyId") Long policyId);
}
