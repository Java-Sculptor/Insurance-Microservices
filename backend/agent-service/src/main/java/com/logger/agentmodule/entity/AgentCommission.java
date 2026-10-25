package com.logger.agentmodule.entity;

import com.logger.agentmodule.enums.AgentCommissionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AgentCommissions")
public class AgentCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommissionId")
    private Long commissionId;

    @Column(name = "AgentId", nullable = false)
    private Long agentId;

    @Column(name = "PolicyId", nullable = false)
    private Long policyId;

    @Column(name = "PremiumAmount", precision = 18, scale = 2,
            nullable = false)
    private BigDecimal premiumAmount;

    @Column(name = "CommissionPercentage", precision = 5, scale = 2,
            nullable = false)
    private BigDecimal commissionPercentage;

    @Column(name = "CommissionAmount", precision = 18, scale = 2,
            nullable = false)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20, nullable = false)
    private AgentCommissionStatus status;

    @Column(name = "EarnedDate", nullable = false)
    private LocalDateTime earnedDate;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (earnedDate == null) {
            earnedDate = LocalDateTime.now();
        }
        if (status == null) {
            status = AgentCommissionStatus.Pending;
        }
    }

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

