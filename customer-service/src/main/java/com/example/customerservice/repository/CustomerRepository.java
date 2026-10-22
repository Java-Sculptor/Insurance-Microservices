package com.example.customerservice.repository;

import com.example.customerservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCustomerCode(String customerCode);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    Page<Customer> findByFirstNameContainingIgnoreCase(
            String firstName,
            Pageable pageable
    );
}
