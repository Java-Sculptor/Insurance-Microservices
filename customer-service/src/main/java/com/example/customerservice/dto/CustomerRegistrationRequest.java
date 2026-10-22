package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.Gender;
import com.example.customerservice.entity.enums.MaritalStatus;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRegistrationRequest {

    // User login details
    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 15)
    private String mobile;

    // Customer details
    @NotBlank
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Gender gender;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotBlank
    @Size(max = 100)
    private String occupation;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal annualIncome;

    @NotBlank
    @Size(max = 15)
    private String mobileNumber;

    @NotBlank
    @Email
    @Size(max = 150)
    private String customerEmail;
}