package com.logger.agentmodule.dto;

import com.logger.agentmodule.enums.AgentCommissionStatus;
import jakarta.validation.constraints.NotNull;

public class AgentCommissionStatusUpdateRequest {

    @NotNull
    private AgentCommissionStatus status;

    public AgentCommissionStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCommissionStatus status) {
        this.status = status;
    }
}
