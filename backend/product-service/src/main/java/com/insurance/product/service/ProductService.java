
package com.insurance.product.service;

import com.insurance.product.dto.ProductRequest;
import com.insurance.product.dto.ProductResponse;

import com.insurance.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    Page<ProductResponse> getAll(
            Long categoryId,
            ProductStatus status,
            Pageable pageable
    );

    ProductResponse getById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse changeStatus(Long id, ProductStatus status);

    ProductResponse publish(Long id);

    void deactivate(Long id);
}
