
package com.insurance.product.repository;

import com.insurance.product.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {

    List<Rider> findByProduct_Id(Long productId);

    List<Rider> findByProduct_IdAndActiveTrue(Long productId);

    boolean existsByProduct_IdAndRiderNameIgnoreCase(
            Long productId,
            String riderName
    );

    boolean existsByProduct_IdAndRiderNameIgnoreCaseAndIdNot(
            Long productId,
            String riderName,
            Long id
    );
}
