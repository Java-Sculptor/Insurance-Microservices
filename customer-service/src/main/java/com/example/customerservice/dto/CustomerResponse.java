package com.example.customerservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import com.example.customerservice.entity.enums.CustomerStatus;
import com.example.customerservice.entity.enums.Gender;
import com.example.customerservice.entity.enums.MaritalStatus;

@Getter
@Setter
public class CustomerResponse {

    private Long customerId;
    private String customerCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String occupation;
    private BigDecimal annualIncome;
    private String mobileNumber;
    private String email;
    private CustomerStatus customerStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}