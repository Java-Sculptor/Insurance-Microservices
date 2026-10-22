package com.example.customerservice.repository;

import com.example.customerservice.entity.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NomineeRepository extends JpaRepository<Nominee, Long> {

    List<Nominee> findByCustomerCustomerId(Long customerId);
}
