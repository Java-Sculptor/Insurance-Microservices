
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.EligibilityRuleRequest;
import com.insurance.product.dto.EligibilityRuleResponse;
import com.insurance.product.entity.EligibilityRule;
import com.insurance.product.entity.Product;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.EligibilityRuleRepository;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.service.EligibilityRuleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class EligibilityRuleServiceImpl
        implements EligibilityRuleService {

    private final EligibilityRuleRepository ruleRepository;
    private final ProductRepository productRepository;

    public EligibilityRuleServiceImpl(
            EligibilityRuleRepository ruleRepository,
            ProductRepository productRepository) {
        this.ruleRepository = ruleRepository;
        this.productRepository = productRepository;
    }

    @Override
    public EligibilityRuleResponse create(
            Long productId,
            EligibilityRuleRequest request) {

        validateRequest(request);

        Product product = findProduct(productId);

        if (ruleRepository.existsByProduct_Id(productId)) {
            throw new DuplicateResourceException(
                    "Eligibility rules already exist for product: "
                            + productId);
        }

        EligibilityRule rule = new EligibilityRule();
        rule.setProduct(product);

        applyRequest(rule, request);

        return toResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityRuleResponse getByProduct(Long productId) {
        return toResponse(findRuleByProduct(productId));
    }

    @Override
    public EligibilityRuleResponse update(
            Long productId,
            EligibilityRuleRequest request) {

        validateRequest(request);

        EligibilityRule rule = findRuleByProduct(productId);
        applyRequest(rule, request);

        return toResponse(ruleRepository.save(rule));
    }

    @Override
    public void delete(Long productId) {
        EligibilityRule rule = findRuleByProduct(productId);
        ruleRepository.delete(rule);
    }

    private void validateRequest(EligibilityRuleRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Eligibility rule request is required.");
        }

        if (request.minimumAge() == null
                || request.maximumAge() == null) {
            throw new IllegalArgumentException(
                    "Minimum age and maximum age are required.");
        }

        if (request.minimumAge() < 0
                || request.maximumAge() > 120) {
            throw new IllegalArgumentException(
                    "Age must be between 0 and 120.");
        }

        if (request.maximumAge() < request.minimumAge()) {
            throw new IllegalArgumentException(
                    "Maximum age must be greater than or equal to minimum age.");
        }

        BigDecimal income = request.minimumIncome();

        if (income == null || income.signum() < 0) {
            throw new IllegalArgumentException(
                    "Minimum income cannot be null or negative.");
        }

        if (request.occupationCategory() == null
                || request.occupationCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Occupation category is required.");
        }

        if (request.occupationCategory().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Occupation category cannot exceed 100 characters.");
        }
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId));
    }

    private EligibilityRule findRuleByProduct(Long productId) {
        findProduct(productId);

        return ruleRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Eligibility rules not found for product: "
                                + productId));
    }

    private void applyRequest(
            EligibilityRule rule,
            EligibilityRuleRequest request) {

        rule.setMinimumAge(request.minimumAge());
        rule.setMaximumAge(request.maximumAge());
        rule.setMinimumIncome(request.minimumIncome());
        rule.setOccupationCategory(request.occupationCategory().trim());
        rule.setMedicalRequired(request.medicalRequired());
    }

    private EligibilityRuleResponse toResponse(EligibilityRule rule) {
        return new EligibilityRuleResponse(
                rule.getId(),
                rule.getProduct().getId(),
                rule.getMinimumAge(),
                rule.getMaximumAge(),
                rule.getMinimumIncome(),
                rule.getOccupationCategory(),
                rule.isMedicalRequired(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }
}
