package com.example.customerservice.dto;

import com.example.customerservice.entity.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {

    private Long addressId;
    private Long customerId;
    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pincode;
}