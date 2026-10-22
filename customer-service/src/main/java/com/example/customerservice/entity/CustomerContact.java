package com.example.customerservice.entity;

import com.example.customerservice.entity.enums.PreferredContactMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_contact")
@Getter
@Setter
public class CustomerContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "alternate_mobile", length = 15)
    private String alternateMobile;

    @Column(name = "office_phone", length = 20)
    private String officePhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_contact_mode")
    private PreferredContactMode preferredContactMode =
            PreferredContactMode.Mobile;
}
