
package com.insurance.product.repository;

import com.insurance.product.entity.Coverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoverageRepository extends JpaRepository<Coverage, Long> {

    List<Coverage> findByProduct_Id(Long productId);

    List<Coverage> findByProduct_IdAndActiveTrue(Long productId);

    boolean existsByProduct_IdAndCoverageNameIgnoreCase(
            Long productId,
            String coverageName
    );

    boolean existsByProduct_IdAndCoverageNameIgnoreCaseAndIdNot(
            Long productId,
            String coverageName,
            Long id
    );
}
