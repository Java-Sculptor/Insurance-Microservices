package com.logger.agentmodule.entity;

import com.logger.agentmodule.enums.AgentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Agents")
@Data
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AgentId")
    private Long  agentId;

    @Column(name = "UserId")
    private  Long userId;

    @NotBlank(message = "Agnet code is required")
    @Size(max=20)
    @Column(name = "AgentCode",nullable = false,unique = true,length = 20)
    private  String agentCode;

    @NotBlank(message = "First Name is required")
    @Size(max =100)
    @Column(name = "FirstName" ,nullable = false,length = 100)
    private  String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max=100)
    @Column(name = "LastName" ,nullable = false,length = 100)
    private String lastName;

    @Email(message = "Enter a valid email")
    @Size(max=150)
    @Column(name = "Email",length = 150)
    private  String email;
    @Size(max = 15)
    @Column
    private  String mobileNo;
    @Column(name = "joiningDate")
    private LocalDate joiningDate;

    @Size(max=50)
    @Column(name = "LicenseNumber",length = 50)
    private  String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private AgentStatus status ;


    @Column(name = "CreatedAt",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name="UpdatedAt")
    private  LocalDateTime updatedAt;

    public Agent() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
