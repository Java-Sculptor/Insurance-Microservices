package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.Occupation;
import com.example.customerservice.entity.enums.EmploymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class OccupationDetailsRequest {

    @NotNull
    private Occupation occupation;

    private String employerName;

    @NotNull
    private EmploymentType employmentType;
}
