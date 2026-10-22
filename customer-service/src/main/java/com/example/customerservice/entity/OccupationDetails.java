package com.example.customerservice.entity;

import com.example.customerservice.entity.enums.EmploymentType;
import com.example.customerservice.entity.enums.Occupation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "occupation_details")
@Getter
@Setter
public class OccupationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupation_details_id")
    private Long occupationDetailsId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "occupation", nullable = false, length = 100)
    private Occupation occupation;

    @Column(name = "employer_name", length = 150)
    private String employerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 20)
    private EmploymentType employmentType;
}
