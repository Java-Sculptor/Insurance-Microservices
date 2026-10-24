package com.app.insurance_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "claim_assessment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long assessmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimMaster claim;

    @Column(name = "assessed_amount", precision = 15, scale = 2)
    private BigDecimal assessedAmount;

    @Column(name = "deductible", precision = 15, scale = 2)
    private BigDecimal deductible;

    @Column(name = "depreciation", precision = 15, scale = 2)
    private BigDecimal depreciation;

    @Column(name = "non_payable", precision = 15, scale = 2)
    private BigDecimal nonPayable;

    @Column(name = "net_payable", precision = 15, scale = 2)
    private BigDecimal netPayable;
}