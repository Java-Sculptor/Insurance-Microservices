package com.app.insurance_management.entity;

import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.enums.ClaimType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "investigation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investigation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investigation_id")
    private Long investigationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimMaster claim;

    // Investigator ID from the relevant service
    @Column(name = "investigator_id")
    private Long investigatorId;

    @Lob
    @Column(name = "investigation_reason")
    private String investigationReason;

    @Column(name = "report_path", length = 500)
    private String reportPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "investigation_status", nullable = false, length = 50)
    private ClaimStatus investigationStatus = ClaimStatus.UNDER_INVESTIGATION;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}