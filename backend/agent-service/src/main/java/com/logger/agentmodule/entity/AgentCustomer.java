package com.logger.agentmodule.entity;
import com.logger.agentmodule.enums.AgentCustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "AgentCustomers")
public class AgentCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AgentCustomerId")
    private Long agentCustomerId;

    @NotNull
    @Column(name = "AgentId", nullable = false)
    private Long agentId;

    @NotNull
    @Column(name = "CustomerId", nullable = false)
    private Long customerId;

    @Column(name = "AssignedDate", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "AssignedBy")
    private Long assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private AgentCustomerStatus status = AgentCustomerStatus.Active;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AgentCustomer() {
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
