
package com.insurance.product.service;

import com.insurance.product.dto.CategoryRequest;
import com.insurance.product.dto.CategoryResponse;

import java.util.List;

public interface ProductCategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> getAll();

    CategoryResponse getById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse changeStatus(Long id, boolean active);

    void deactivate(Long id);
}
