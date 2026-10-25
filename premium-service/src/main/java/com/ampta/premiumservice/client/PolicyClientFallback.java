package com.ampta.premiumservice.client;

import com.ampta.premiumservice.dto.ApiResponse;
import com.ampta.premiumservice.dto.PolicyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PolicyClientFallback implements PolicyClient {

    @Override
    public ApiResponse<PolicyDto> getPolicy(Long id) {
        log.warn("PolicyClientFallback triggered for getPolicy ID: {}", id);
        PolicyDto mock = new PolicyDto(
                id,
                "POL-FALLBACK-" + id,
                101L,
                1L,
                10L,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "PENDING_PAYMENT",
                "YEARLY",
                BigDecimal.valueOf(5000)
        );
        return new ApiResponse<>(true, "Fallback policy data", mock);
    }

    @Override
    public ApiResponse<List<PolicyDto>> getAllPolicies() {
        log.warn("PolicyClientFallback triggered for getAllPolicies");
        return new ApiResponse<>(true, "Fallback policy list", Collections.emptyList());
    }

    @Override
    public ApiResponse<PolicyDto> activatePolicy(Long id) {
        log.warn("PolicyClientFallback triggered for activatePolicy ID: {}", id);
        PolicyDto mock = new PolicyDto(
                id,
                "POL-FALLBACK-" + id,
                101L,
                1L,
                10L,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "ACTIVE",
                "YEARLY",
                BigDecimal.valueOf(5000)
        );
        return new ApiResponse<>(true, "Fallback policy activated", mock);
    }

    @Override
    public ApiResponse<PolicyDto> suspendPolicy(Long id) {
        log.warn("PolicyClientFallback triggered for suspendPolicy ID: {}", id);
        PolicyDto mock = new PolicyDto(
                id,
                "POL-FALLBACK-" + id,
                101L,
                1L,
                10L,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "SUSPENDED",
                "YEARLY",
                BigDecimal.valueOf(5000)
        );
        return new ApiResponse<>(true, "Fallback policy suspended", mock);
    }

    @Override
    public ApiResponse<Void> cancelPolicy(Long id) {
        log.warn("PolicyClientFallback triggered for cancelPolicy ID: {}", id);
        return new ApiResponse<>(true, "Fallback policy cancelled", null);
    }
}

