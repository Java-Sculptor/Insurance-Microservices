package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.PreferredContactMode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ContactRequest {

    private String alternateMobile;

    private String officePhone;

    private PreferredContactMode preferredContactMode;
}
