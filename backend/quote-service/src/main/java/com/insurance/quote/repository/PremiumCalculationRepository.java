
package com.insurance.quote.repository;


import com.insurance.quote.entity.PremiumCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PremiumCalculationRepository
        extends JpaRepository<PremiumCalculation, Long> {

    Optional<PremiumCalculation> findByQuote_QuoteId(Long quoteId);


}
