
package com.insurance.product.repository;

import com.insurance.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, Long> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByCategoryNameIgnoreCaseAndIdNot(
            String categoryName, Long id
    );
}
