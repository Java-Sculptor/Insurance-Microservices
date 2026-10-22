package com.example.customerservice.repository;

import com.example.customerservice.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerDocumentRepository
        extends JpaRepository<CustomerDocument, Long> {

    List<CustomerDocument> findByCustomerCustomerId(Long customerId);
}
