
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.BenefitRequest;
import com.insurance.product.dto.BenefitResponse;
import com.insurance.product.entity.Product;
import com.insurance.product.entity.ProductBenefit;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.ProductBenefitRepository;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.service.ProductBenefitService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductBenefitServiceImpl implements ProductBenefitService {

    private final ProductBenefitRepository benefitRepository;
    private final ProductRepository productRepository;

    public ProductBenefitServiceImpl(
            ProductBenefitRepository benefitRepository,
            ProductRepository productRepository) {
        this.benefitRepository = benefitRepository;
        this.productRepository = productRepository;
    }

    @Override
    public BenefitResponse create(Long productId, BenefitRequest request) {
        Product product = findProduct(productId);

        String name = request.benefitName().trim();

        if (benefitRepository.existsByProduct_IdAndBenefitNameIgnoreCase(
                productId, name)) {
            throw new DuplicateResourceException(
                    "Benefit already exists for this product: " + name);
        }

        ProductBenefit benefit = new ProductBenefit();
        benefit.setProduct(product);
        applyRequest(benefit, request);

        return toResponse(benefitRepository.save(benefit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> getByProduct(Long productId) {
        findProduct(productId);

        return benefitRepository.findByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BenefitResponse update(Long benefitId, BenefitRequest request) {
        ProductBenefit benefit = findBenefit(benefitId);

        String name = request.benefitName().trim();

        if (benefitRepository
                .existsByProduct_IdAndBenefitNameIgnoreCaseAndIdNot(
                        benefit.getProduct().getId(),
                        name,
                        benefitId)) {
            throw new DuplicateResourceException(
                    "Benefit already exists for this product: " + name);
        }

        applyRequest(benefit, request);

        return toResponse(benefitRepository.save(benefit));
    }

    @Override
    public void delete(Long benefitId) {
        ProductBenefit benefit = findBenefit(benefitId);
        benefit.setActive(false);
        benefitRepository.save(benefit);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId));
    }

    private ProductBenefit findBenefit(Long benefitId) {
        return benefitRepository.findById(benefitId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Benefit not found: " + benefitId));
    }

    private void applyRequest(
            ProductBenefit benefit,
            BenefitRequest request) {

        benefit.setBenefitName(request.benefitName().trim());
        benefit.setDescription(request.description());
        benefit.setCoverageAmount(request.coverageAmount());
    }

    private BenefitResponse toResponse(ProductBenefit benefit) {
        return new BenefitResponse(
                benefit.getId(),
                benefit.getProduct().getId(),
                benefit.getBenefitName(),
                benefit.getDescription(),
                benefit.getCoverageAmount(),
                benefit.isActive()
        );
    }
}
