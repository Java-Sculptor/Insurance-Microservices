package com.logger.agentmodule.dto;
import jakarta.validation.constraints.NotNull;

public class AgentCustomerTransferRequest {

    @NotNull(message = "New agent ID is required")
    private Long newAgentId;

    @NotNull(message = "Assigned by user ID is required")
    private Long assignedBy;

    public Long getNewAgentId() {
        return newAgentId;
    }

    public void setNewAgentId(Long newAgentId) {
        this.newAgentId = newAgentId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }
}
