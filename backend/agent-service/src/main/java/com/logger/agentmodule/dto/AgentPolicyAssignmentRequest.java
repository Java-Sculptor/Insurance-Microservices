package com.logger.agentmodule.dto;
import jakarta.validation.constraints.NotNull;

public class AgentPolicyAssignmentRequest {

    @NotNull
    private Long policyId;

    @NotNull
    private Long customerId;

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}

