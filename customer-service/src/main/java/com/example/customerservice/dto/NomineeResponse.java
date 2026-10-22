package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.NomineeRelationship;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class NomineeResponse {

    private Long nomineeId;
    private Long customerId;
    private String nomineeName;
    private NomineeRelationship relationship;
    private LocalDate dateOfBirth;
    private String mobileNumber;
    private BigDecimal sharePercentage;
}
