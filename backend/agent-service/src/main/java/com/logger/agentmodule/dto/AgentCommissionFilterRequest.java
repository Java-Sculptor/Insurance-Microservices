package com.logger.agentmodule.dto;
import com.logger.agentmodule.enums.AgentCommissionStatus;
import java.time.LocalDate;

public class AgentCommissionFilterRequest {

    private Long agentId;

    private AgentCommissionStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public AgentCommissionStatus getStatus() {
        return status;
    }

    public void setStatus(AgentCommissionStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
