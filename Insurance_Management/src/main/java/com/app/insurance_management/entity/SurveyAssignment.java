package com.app.insurance_management.entity;

import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.enums.SurveyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "survey_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long surveyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimMaster claim;

    // Employee/Surveyor ID from the relevant service
    @Column(name = "surveyor_id")
    private Long surveyorId;

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;

    @Column(name = "report_path", length = 500)
    private String reportPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "survey_status", length = 20)
    private SurveyStatus surveyStatus = SurveyStatus.ASSIGNED;

    @Column(name = "damage_description", length = 2000)
    private String damageDescription;

    @Column(name = "assessed_damage_amount", precision = 15, scale = 2)
    private BigDecimal assessedDamageAmount;

    @Column(name = "surveyor_remarks", length = 2000)
    private String surveyorRemarks;

    @Column(name = "recommendation", length = 1000)
    private String recommendation;
}