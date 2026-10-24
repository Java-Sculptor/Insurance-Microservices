
package com.insurance.product.repository;

import com.insurance.product.entity.CommissionConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommissionConfigurationRepository
        extends JpaRepository<CommissionConfiguration, Long> {

    Optional<CommissionConfiguration> findByProductCategory_Id(
            Long categoryId);

    boolean existsByProductCategory_Id(Long categoryId);
}
