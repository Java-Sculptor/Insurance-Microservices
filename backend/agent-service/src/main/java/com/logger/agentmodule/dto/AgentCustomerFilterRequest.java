package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentCustomerStatus;

public class AgentCustomerFilterRequest {

    private Long agentId;

    private AgentCustomerStatus status;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public AgentCustomerStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCustomerStatus status) {
        this.status = status;
    }
}
