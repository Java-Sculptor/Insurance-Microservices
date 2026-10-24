
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.ProductRequest;
import com.insurance.product.dto.ProductResponse;
import com.insurance.product.entity.Product;
import com.insurance.product.entity.ProductCategory;
import com.insurance.product.enums.ProductStatus;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.repository.ProductCategoryRepository;
import com.insurance.product.service.ProductService;
import com.insurance.product.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        validateRequest(request);

        if (productRepository.existsByProductCode(request.productCode())) {
            throw new IllegalArgumentException("Product code already exists");
        }

        if (productRepository.existsByProductNameIgnoreCase(request.productName())) {
            throw new IllegalArgumentException("Product name already exists");
        }

        ProductCategory category = findCategory(request.categoryId());

        Product product = new Product();
        applyRequest(product, request);
        product.setCategory(category);
        product.setStatus(ProductStatus.DRAFT);

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(
            Long categoryId,
            ProductStatus status,
            Pageable pageable) {

        Page<Product> products;

        if (categoryId != null && status != null) {
            products = productRepository.findByCategory_IdAndStatus(
                    categoryId, status, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategory_Id(
                    categoryId, pageable);
        } else if (status != null) {
            products = productRepository.findByStatus(status, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(findProduct(id));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        validateRequest(request);

        Product product = findProduct(id);

        if (productRepository.existsByProductCodeAndIdNot(
                request.productCode(), id)) {
            throw new IllegalArgumentException("Product code already exists");
        }

        if (productRepository.existsByProductNameIgnoreCaseAndIdNot(
                request.productName(), id)) {
            throw new IllegalArgumentException("Product name already exists");
        }

        ProductCategory category = findCategory(request.categoryId());

        if (product.getStatus() == ProductStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Deactivate the product before changing its configuration");
        }

        applyRequest(product, request);
        product.setCategory(category);

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse changeStatus(Long id, ProductStatus status) {
        Product product = findProduct(id);

        if (status == ProductStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Use the publish operation to activate a product");
        }

        product.setStatus(status);
        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse publish(Long id) {
        Product product = findProduct(id);

        if (product.getStatus() == ProductStatus.ACTIVE) {
            return toResponse(product);
        }

        validateProductForPublishing(product);

        product.setStatus(ProductStatus.ACTIVE);
        return toResponse(productRepository.save(product));
    }

    @Override
    public void deactivate(Long id) {
        Product product = findProduct(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));
    }

    private ProductCategory findCategory(Long categoryId) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + categoryId));

        if (!category.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot use an inactive product category");
        }

        return category;
    }

    private void validateRequest(ProductRequest request) {
        if (request.minAge() != null
                && request.maxAge() != null
                && request.minAge() > request.maxAge()) {
            throw new IllegalArgumentException(
                    "Minimum age cannot exceed maximum age");
        }

        if (request.minSumInsured() != null
                && request.maxSumInsured() != null
                && request.minSumInsured()
                .compareTo(request.maxSumInsured()) > 0) {
            throw new IllegalArgumentException(
                    "Minimum sum insured cannot exceed maximum sum insured");
        }

        if (request.minPolicyTerm() != null
                && request.maxPolicyTerm() != null
                && request.minPolicyTerm() > request.maxPolicyTerm()) {
            throw new IllegalArgumentException(
                    "Minimum policy term cannot exceed maximum policy term");
        }
    }

    private void validateProductForPublishing(Product product) {
        if (product.getCategory() == null
                || !product.getCategory().isActive()) {
            throw new IllegalStateException(
                    "An active product category is required");
        }

        if (product.getMinAge() == null
                || product.getMaxAge() == null
                || product.getMinSumInsured() == null
                || product.getMaxSumInsured() == null
                || product.getMinPolicyTerm() == null
                || product.getMaxPolicyTerm() == null
                || product.getPremiumFrequency() == null
                || product.getPremiumFrequency().isBlank()) {
            throw new IllegalStateException(
                    "Complete the required product configuration before publishing");
        }

        if (product.getMinAge() > product.getMaxAge()
                || product.getMinSumInsured()
                .compareTo(product.getMaxSumInsured()) > 0
                || product.getMinPolicyTerm() > product.getMaxPolicyTerm()) {
            throw new IllegalStateException(
                    "Product configuration contains invalid limits");
        }
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setProductCode(request.productCode());
        product.setProductName(request.productName());
        product.setProductType(request.productType());
        product.setDescription(request.description());
        product.setMinAge(request.minAge());
        product.setMaxAge(request.maxAge());
        product.setMinSumInsured(request.minSumInsured());
        product.setMaxSumInsured(request.maxSumInsured());
        product.setMinPolicyTerm(request.minPolicyTerm());
        product.setMaxPolicyTerm(request.maxPolicyTerm());
        product.setPremiumFrequency(request.premiumFrequency());
        product.setWaitingPeriodDays(
                request.waitingPeriodDays() == null
                        ? 0 : request.waitingPeriodDays());
        product.setGracePeriodDays(
                request.gracePeriodDays() == null
                        ? 0 : request.gracePeriodDays());
        product.setGstPercentage(
                request.gstPercentage() == null
                        ? BigDecimal.ZERO : request.gstPercentage());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                product.getProductType(),
                product.getDescription(),
                product.getCategory().getId(),
                product.getCategory().getCategoryName(),
                product.getMinAge(),
                product.getMaxAge(),
                product.getMinSumInsured(),
                product.getMaxSumInsured(),
                product.getMinPolicyTerm(),
                product.getMaxPolicyTerm(),
                product.getPremiumFrequency(),
                product.getWaitingPeriodDays(),
                product.getGracePeriodDays(),
                product.getGstPercentage(),
                product.getStatus().name(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
