package com.logger.agentmodule.entity;


import com.logger.agentmodule.enums.AgentPolicyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "AgentPolicies")
public class AgentPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AgentPolicyId")
    private Long agentPolicyId;

    @NotNull
    @Column(name = "AgentId", nullable = false)
    private Long agentId;

    @NotNull
    @Column(name = "PolicyId", nullable = false)
    private Long policyId;

    @NotNull
    @Column(name = "CustomerId", nullable = false)
    private Long customerId;

    @Column(name = "AssignedDate", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "SaleDate")
    private LocalDateTime saleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private AgentPolicyStatus status = AgentPolicyStatus.Assigned;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AgentPolicy() {
    }

    @PrePersist
    protected void onCreate() {
        if (assignedDate == null) {
            assignedDate = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

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

