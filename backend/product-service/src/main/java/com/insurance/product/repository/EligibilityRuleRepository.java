
package com.insurance.product.repository;

import com.insurance.product.entity.EligibilityRule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibilityRuleRepository
        extends JpaRepository<EligibilityRule, Long> {

    Optional<EligibilityRule> findByProduct_Id(Long productId);

    boolean existsByProduct_Id(Long productId);
}
