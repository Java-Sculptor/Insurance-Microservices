
package com.insurance.product.repository;

import com.insurance.product.entity.PremiumFrequency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PremiumFrequencyRepository
        extends JpaRepository<PremiumFrequency, Long> {

    List<PremiumFrequency> findByProduct_Id(Long productId);

    Optional<PremiumFrequency> findByProduct_IdAndFrequency(
            Long productId,
            String frequency
    );

    boolean existsByProduct_IdAndFrequency(
            Long productId,
            String frequency
    );
}
