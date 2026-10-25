package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentCustomerStatus;

import java.time.LocalDateTime;

public class AgentCustomerResponse {

    private Long agentCustomerId;
    private Long agentId;
    private Long customerId;
    private LocalDateTime assignedDate;
    private Long assignedBy;
    private AgentCustomerStatus status;
    private LocalDateTime createdAt;

    public Long getAgentCustomerId() {
        return agentCustomerId;
    }

    public void setAgentCustomerId(Long agentCustomerId) {
        this.agentCustomerId = agentCustomerId;
    }

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

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public AgentCustomerStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCustomerStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

