package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentCommissionPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgentCommissionPaymentResponse {

    private Long commissionPaymentId;
    private Long agentId;
    private String paymentReference;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String transactionReference;
    private AgentCommissionPaymentStatus status;
    private LocalDateTime createdAt;

    public Long getCommissionPaymentId() {
        return commissionPaymentId;
    }

    public void setCommissionPaymentId(Long commissionPaymentId) {
        this.commissionPaymentId = commissionPaymentId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public AgentCommissionPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCommissionPaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
