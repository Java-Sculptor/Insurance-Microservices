package com.example.customerservice.service;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse addAddress(Long customerId, AddressRequest request);

    List<AddressResponse> getAddressesByCustomerId(Long customerId);

    AddressResponse updateAddress(Long addressId, AddressRequest request);

    void deleteAddress(Long addressId);
}
