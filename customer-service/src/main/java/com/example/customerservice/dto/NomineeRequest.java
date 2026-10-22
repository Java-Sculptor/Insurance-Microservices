package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.NomineeRelationship;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class NomineeRequest {

    @NotBlank
    private String nomineeName;

    @NotNull
    private NomineeRelationship relationship;

    @NotNull
    private LocalDate dateOfBirth;

    private String mobileNumber;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal sharePercentage;
}
