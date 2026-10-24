
package com.insurance.product.repository;

import com.insurance.product.entity.SumInsuredConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SumInsuredConfigurationRepository
        extends JpaRepository<SumInsuredConfiguration, Long> {

    List<SumInsuredConfiguration> findByProduct_Id(Long productId);

    Optional<SumInsuredConfiguration> findByIdAndProduct_Id(
            Long id,
            Long productId
    );

    boolean existsByProduct_Id(Long productId);
}
