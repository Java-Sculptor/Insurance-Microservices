package com.logger.agentmodule.entity;
import com.logger.agentmodule.enums.AgentCommissionPaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "AgentCommissionPayments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_PaymentReference",
                        columnNames = "PaymentReference"
                )
        }
)
public class AgentCommissionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommissionPaymentId")
    private Long commissionPaymentId;

    @NotNull
    @Column(name = "AgentId", nullable = false)
    private Long agentId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "PaymentReference", nullable = false, length = 100)
    private String paymentReference;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "TotalAmount", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @NotBlank
    @Size(max = 30)
    @Column(name = "PaymentMode", length = 30)
    private String paymentMode;

    @Size(max = 100)
    @Column(name = "TransactionReference", length = 100)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    private AgentCommissionPaymentStatus status =
            AgentCommissionPaymentStatus.Pending;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = AgentCommissionPaymentStatus.Pending;
        }
    }

    // Generate getters and setters using IntelliJ:
    // Alt + Insert -> Getter and Setter
}

