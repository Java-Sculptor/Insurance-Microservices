package com.insurance.quote.repository;


import com.insurance.quote.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

    Optional<RiskAssessment> findByQuote_QuoteId(Long quoteId);
}