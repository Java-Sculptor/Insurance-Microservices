package com.example.customerservice.service;

import com.example.customerservice.dto.AddressRequest;
import com.example.customerservice.dto.ContactRequest;
import com.example.customerservice.dto.CustomerRegistrationRequest;
import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.NomineeRequest;

import com.example.customerservice.entity.Customer;
import com.example.customerservice.entity.CustomerAddress;
import com.example.customerservice.entity.CustomerContact;
import com.example.customerservice.entity.Nominee;
import com.example.customerservice.entity.OccupationDetails;
import com.example.customerservice.entity.Role;
import com.example.customerservice.entity.User;

import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.exception.DuplicateResourceException;
import com.example.customerservice.exception.ResourceNotFoundException;

import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.repository.OccupationDetailsRepository;
import com.example.customerservice.repository.RoleRepository;
import com.example.customerservice.repository.UserRepository;
import com.example.customerservice.service.CustomerService;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OccupationDetailsRepository occupationDetailsRepository;
    private final ModelMapper modelMapper;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            OccupationDetailsRepository occupationDetailsRepository,
            ModelMapper modelMapper) {

        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.occupationDetailsRepository = occupationDetailsRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional
    public CustomerResponse registerCustomer(
            CustomerRegistrationRequest request) {

        // Check duplicate login details
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Login email already exists");
        }

        // Check duplicate customer details
        if (customerRepository.existsByMobileNumber(
                request.getMobileNumber())) {
            throw new DuplicateResourceException(
                    "Customer mobile number already exists");
        }

        if (customerRepository.existsByEmail(
                request.getCustomerEmail())) {
            throw new DuplicateResourceException(
                    "Customer email already exists");
        }

        // Find CUSTOMER role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CUSTOMER role not found"));

        // Create login user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setRole(customerRole);

        User savedUser = userRepository.save(user);

        // Create customer
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmail(request.getCustomerEmail());

        Customer savedCustomer = customerRepository.save(customer);

        return modelMapper.map(savedCustomer, CustomerResponse.class);
    }

    @Override
    public Page<CustomerResponse> getCustomers(int page) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        PageRequest pageable = PageRequest.of(page, 5);

        return customerRepository.findAll(pageable)
                .map(customer ->
                        modelMapper.map(customer, CustomerResponse.class)
                );
    }


    @Override
    public CustomerResponse getCustomerById(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with ID: " + customerId
                        )
                );

        return modelMapper.map(customer, CustomerResponse.class);
    }


    @Override
    @Transactional
    public CustomerResponse updateCustomer(
            Long customerId,
            CustomerRegistrationRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId));

        // Update customer profile fields only
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setOccupation(request.getOccupation());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmail(request.getCustomerEmail());

        Customer savedCustomer = customerRepository.save(customer);

        return modelMapper.map(savedCustomer, CustomerResponse.class);
    }


    @Override
    public Page<CustomerResponse> searchCustomers(
            String firstName, int page) {

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "First name cannot be empty");
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        PageRequest pageable = PageRequest.of(page, 5);

        return customerRepository
                .findByFirstNameContainingIgnoreCase(
                        firstName.trim(), pageable)
                .map(customer ->
                        modelMapper.map(customer, CustomerResponse.class));
    }

    @Override
    public void deleteCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId
                ));

        customerRepository.delete(customer);
    }


}
