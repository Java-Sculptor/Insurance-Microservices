package com.example.customerservice.repository;

import com.example.customerservice.entity.OccupationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OccupationDetailsRepository
        extends JpaRepository<OccupationDetails, Long> {

    Optional<OccupationDetails> findByCustomer_CustomerId(Long customerId);
}
