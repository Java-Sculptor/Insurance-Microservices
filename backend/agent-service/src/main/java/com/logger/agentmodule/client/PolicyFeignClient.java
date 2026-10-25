package com.logger.agentmodule.client;

import com.logger.agentmodule.dto.PolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "insurance-management-policy-issuance",
        url = "${policy.service.url}"
)
public interface PolicyFeignClient {

    @GetMapping("/api/v1/policies/{id}")
    Object getPolicyById(@PathVariable("id") Long id);
}