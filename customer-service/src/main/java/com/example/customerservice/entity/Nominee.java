package com.example.customerservice.entity;

import com.example.customerservice.entity.enums.NomineeRelationship;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "nominee")
@Getter
@Setter
public class Nominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nominee_id")
    private Long nomineeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "nominee_name", nullable = false, length = 150)
    private String nomineeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 50)
    private NomineeRelationship relationship;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "share_percentage",
            nullable = false, precision = 5, scale = 2)
    private BigDecimal sharePercentage;
}