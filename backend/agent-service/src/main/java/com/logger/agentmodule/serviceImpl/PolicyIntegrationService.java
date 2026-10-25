package com.logger.agentmodule.serviceImpl;
import com.logger.agentmodule.client.PolicyFeignClient;
import org.springframework.stereotype.Service;

@Service
public class PolicyIntegrationService {

    private final PolicyFeignClient policyFeignClient;

    public PolicyIntegrationService(PolicyFeignClient policyFeignClient) {
        this.policyFeignClient = policyFeignClient;
    }

    public Object getPolicy(Long policyId) {
        return policyFeignClient.getPolicyById(policyId);
    }
}
