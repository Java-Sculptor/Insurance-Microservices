package com.logger.agentmodule.dto;

import jakarta.validation.constraints.NotNull;

public class AgentCustomerAssignmentRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long assignedBy;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }
}

