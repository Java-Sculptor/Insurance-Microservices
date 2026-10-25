package com.logger.agentmodule.dto;

import com.logger.agentmodule.enums.AgentCommissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgentCommissionResponse {

    private Long commissionId;
    private Long agentId;
    private Long policyId;
    private BigDecimal premiumAmount;
    private BigDecimal commissionPercentage;
    private BigDecimal commissionAmount;
    private AgentCommissionStatus status;
    private LocalDateTime earnedDate;
    private LocalDateTime createdAt;

    public Long getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(Long commissionId) {
        this.commissionId = commissionId;
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

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public AgentCommissionStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCommissionStatus status) {
        this.status = status;
    }

    public LocalDateTime getEarnedDate() {
        return earnedDate;
    }

    public void setEarnedDate(LocalDateTime earnedDate) {
        this.earnedDate = earnedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
