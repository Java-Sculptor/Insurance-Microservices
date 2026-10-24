
package com.insurance.product.repository;

import com.insurance.product.entity.ProductBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBenefitRepository
        extends JpaRepository<ProductBenefit, Long> {

    List<ProductBenefit> findByProduct_Id(Long productId);

    List<ProductBenefit> findByProduct_IdAndActiveTrue(Long productId);

    boolean existsByProduct_IdAndBenefitNameIgnoreCase(
            Long productId,
            String benefitName
    );

    boolean existsByProduct_IdAndBenefitNameIgnoreCaseAndIdNot(
            Long productId,
            String benefitName,
            Long id
    );
}
