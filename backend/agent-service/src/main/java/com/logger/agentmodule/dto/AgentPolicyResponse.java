package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentPolicyStatus;

import java.time.LocalDateTime;

public class AgentPolicyResponse {

    private Long agentPolicyId;
    private Long agentId;
    private Long policyId;
    private Long customerId;
    private LocalDateTime assignedDate;
    private LocalDateTime saleDate;
    private AgentPolicyStatus status;
    private LocalDateTime createdAt;

    public Long getAgentPolicyId() {
        return agentPolicyId;
    }

    public void setAgentPolicyId(Long agentPolicyId) {
        this.agentPolicyId = agentPolicyId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

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

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public AgentPolicyStatus getStatus() {
        return status;
    }

    public void setStatus(AgentPolicyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

