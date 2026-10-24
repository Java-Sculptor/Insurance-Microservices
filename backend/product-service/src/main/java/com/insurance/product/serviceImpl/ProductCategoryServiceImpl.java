
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.CategoryRequest;
import com.insurance.product.dto.CategoryResponse;
import com.insurance.product.entity.ProductCategory;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.ProductCategoryRepository;
import com.insurance.product.service.ProductCategoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository repository;

    public ProductCategoryServiceImpl(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String name = request.categoryName().trim();

        if (repository.existsByCategoryNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "Category name already exists: " + name
            );
        }

        ProductCategory category = new ProductCategory();
        category.setCategoryName(name);
        category.setDescription(request.description());

        return toResponse(repository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return toResponse(findCategory(id));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        ProductCategory category = findCategory(id);
        String name = request.categoryName().trim();

        if (repository.existsByCategoryNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException(
                    "Category name already exists: " + name
            );
        }

        category.setCategoryName(name);
        category.setDescription(request.description());

        return toResponse(repository.save(category));
    }

    @Override
    public CategoryResponse changeStatus(Long id, boolean active) {
        ProductCategory category = findCategory(id);
        category.setActive(active);

        return toResponse(repository.save(category));
    }

    @Override
    public void deactivate(Long id) {
        ProductCategory category = findCategory(id);
        category.setActive(false);
        repository.save(category);
    }

    private ProductCategory findCategory(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + id
                ));
    }

    private CategoryResponse toResponse(ProductCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getDescription(),
                category.isActive()
        );
    }
}
