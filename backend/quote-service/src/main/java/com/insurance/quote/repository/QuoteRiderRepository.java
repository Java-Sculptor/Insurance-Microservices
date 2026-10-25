
package com.insurance.quote.repository;


import com.insurance.quote.entity.QuoteRider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRiderRepository
        extends JpaRepository<QuoteRider, Long> {

    List<QuoteRider> findByQuote_QuoteId(Long quoteId);
}
