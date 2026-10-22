package com.example.customerservice.controller;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.AddressResponse;
import com.example.customerservice.service.AddressService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // 1. Add address for a customer
    @PostMapping("/customers/{id}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable("id") Long customerId,
            @Valid @RequestBody AddressRequest request) {

        AddressResponse response =
                addressService.addAddress(customerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all addresses for a customer
    @GetMapping("/customers/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @PathVariable("id") Long customerId) {

        List<AddressResponse> addresses =
                addressService.getAddressesByCustomerId(customerId);

        return ResponseEntity.ok(addresses);
    }

    // 3. Update an address
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {

        AddressResponse response =
                addressService.updateAddress(addressId, request);

        return ResponseEntity.ok(response);
    }

    // 4. Delete an address
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}
