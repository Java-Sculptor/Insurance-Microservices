
package com.app.insurance_management.entity;

import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.enums.ClaimType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claim_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "claim_number", unique = true, nullable = false, length = 30)
    private String claimNumber;

    // IDs belong to other modules/services
    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false, length = 50)
    private ClaimType claimType;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "claim_date")
    private LocalDate claimDate;

    @Column(name = "estimated_loss", precision = 15, scale = 2)
    private BigDecimal estimatedLoss;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false, length = 50)
    private ClaimStatus claimStatus = ClaimStatus.REGISTERED;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}
