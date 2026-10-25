package com.logger.agentmodule.repository;
import com.logger.agentmodule.entity.CommissionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommissionRuleRepository
        extends JpaRepository<CommissionRule, Long> {

    List<CommissionRule> findByProductId(Long productId);

    List<CommissionRule> findByProductIdAndIsActiveTrue(
            Long productId
    );

    List<CommissionRule> findByIsActiveTrue();

    List<CommissionRule> findByEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
            LocalDate effectiveDate1,
            LocalDate effectiveDate2
    );
}

