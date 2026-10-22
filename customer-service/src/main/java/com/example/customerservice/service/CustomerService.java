package com.example.customerservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.customerservice.dto.CustomerRegistrationRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.entity.Customer;

public interface CustomerService {

    CustomerResponse registerCustomer(
            CustomerRegistrationRequest request);

    Page<CustomerResponse> getCustomers(int page);
    CustomerResponse getCustomerById(Long customerId);
    CustomerResponse updateCustomer(
            Long customerId,
            CustomerRegistrationRequest request
    );
    Page<CustomerResponse> searchCustomers(String firstName, int page);
    void deleteCustomer(Long customerId);
}
