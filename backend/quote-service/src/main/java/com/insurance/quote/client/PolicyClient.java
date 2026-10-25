package com.insurance.quote.client;

import com.insurance.quote.dto.PolicyCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "policy-service", url = "${policy.service.url:}")
public interface PolicyClient {

    @PostMapping("/api/v1/policies")
    String createPolicy(@RequestBody PolicyCreationRequest request);
}
