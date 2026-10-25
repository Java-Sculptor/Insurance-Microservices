package com.ampta.premiumservice.client;

import com.ampta.premiumservice.dto.ApiResponse;
import com.ampta.premiumservice.dto.PolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(
        name = "insurance-management-policy-issuance",
        url = "${policy.service.url:http://localhost:8088}",
        fallback = PolicyClientFallback.class
)
public interface PolicyClient {

    @GetMapping("/api/v1/policies/{id}")
    ApiResponse<PolicyDto> getPolicy(@PathVariable("id") Long id);

    @GetMapping("/api/v1/policies")
    ApiResponse<List<PolicyDto>> getAllPolicies();

    @PutMapping("/api/v1/policies/{id}/activate")
    ApiResponse<PolicyDto> activatePolicy(@PathVariable("id") Long id);

    @PutMapping("/api/v1/policies/{id}/suspend")
    ApiResponse<PolicyDto> suspendPolicy(@PathVariable("id") Long id);

    @PutMapping("/api/v1/policies/{id}/cancel")
    ApiResponse<Void> cancelPolicy(@PathVariable("id") Long id);
}

