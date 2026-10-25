package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentCommissionPaymentStatus;

public class AgentCommissionPaymentFilterRequest {

    private Long agentId;

    private AgentCommissionPaymentStatus status;

    private String paymentMode;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public AgentCommissionPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCommissionPaymentStatus status) {
        this.status = status;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
