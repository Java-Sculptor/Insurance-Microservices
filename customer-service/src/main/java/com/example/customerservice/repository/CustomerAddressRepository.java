package com.example.customerservice.repository;


import com.example.customerservice.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomerCustomerId(Long customerId);
}
