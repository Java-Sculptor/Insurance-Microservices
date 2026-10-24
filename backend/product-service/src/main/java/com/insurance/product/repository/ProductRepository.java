
package com.insurance.product.repository;

import com.insurance.product.entity.Product;
import com.insurance.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductCode(String productCode);

    boolean existsByProductNameIgnoreCase(String productName);

    boolean existsByProductCodeAndIdNot(String productCode, Long id);

    boolean existsByProductNameIgnoreCaseAndIdNot(
            String productName,
            Long id
    );

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByCategory_IdAndStatus(
            Long categoryId,
            ProductStatus status,
            Pageable pageable
    );

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByStatus(ProductStatus status);
}
