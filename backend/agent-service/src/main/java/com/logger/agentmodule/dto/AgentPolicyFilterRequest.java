package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentPolicyStatus;

public class AgentPolicyFilterRequest {

    private Long agentId;

    private Long customerId;

    private AgentPolicyStatus status;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public AgentPolicyStatus getStatus() {
        return status;
    }

    public void setStatus(AgentPolicyStatus status) {
        this.status = status;
    }
}
