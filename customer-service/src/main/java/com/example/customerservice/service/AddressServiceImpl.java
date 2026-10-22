package com.example.customerservice.service;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.AddressResponse;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.entity.CustomerAddress;
import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.repository.CustomerAddressRepository;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.AddressService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final CustomerAddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressServiceImpl(
            CustomerAddressRepository addressRepository,
            CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public AddressResponse addAddress(
            Long customerId, AddressRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId));

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);

        mapRequestToEntity(request, address);

        CustomerAddress savedAddress = addressRepository.save(address);

        return mapToResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddressesByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return addressRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(
            Long addressId, AddressRequest request) {

        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new com.example.customerservice.exception.ResourceNotFoundException(
                        "Address not found with ID: " + addressId));

        mapRequestToEntity(request, address);

        CustomerAddress updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {

        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new com.example.customerservice.exception.ResourceNotFoundException(
                        "Address not found with ID: " + addressId));

        addressRepository.delete(address);
    }

    private void mapRequestToEntity(
            AddressRequest request, CustomerAddress address) {

        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPincode(request.getPincode());
    }

    private AddressResponse mapToResponse(CustomerAddress address) {

        AddressResponse response = new AddressResponse();

        response.setAddressId(address.getAddressId());
        response.setCustomerId(address.getCustomer().getCustomerId());
        response.setAddressType(address.getAddressType());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPincode(address.getPincode());

        return response;
    }
}
