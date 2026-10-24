
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.ProductResponse;
import com.insurance.product.entity.Product;
import com.insurance.product.entity.SumInsuredConfiguration;
import com.insurance.product.enums.ProductStatus;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.CommissionConfigurationRepository;
import com.insurance.product.repository.CoverageRepository;
import com.insurance.product.repository.EligibilityRuleRepository;
import com.insurance.product.repository.PremiumFrequencyRepository;
import com.insurance.product.repository.ProductBenefitRepository;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.repository.SumInsuredConfigurationRepository;
import com.insurance.product.service.ProductPublishingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductPublishingServiceImpl
        implements ProductPublishingService {

    private final ProductRepository productRepository;
    private final CoverageRepository coverageRepository;
    private final ProductBenefitRepository benefitRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final PremiumFrequencyRepository premiumFrequencyRepository;
    private final SumInsuredConfigurationRepository sumInsuredRepository;
    private final CommissionConfigurationRepository commissionRepository;

    public ProductPublishingServiceImpl(
            ProductRepository productRepository,
            CoverageRepository coverageRepository,
            ProductBenefitRepository benefitRepository,
            EligibilityRuleRepository eligibilityRuleRepository,
            PremiumFrequencyRepository premiumFrequencyRepository,
            SumInsuredConfigurationRepository sumInsuredRepository,
            CommissionConfigurationRepository commissionRepository) {

        this.productRepository = productRepository;
        this.coverageRepository = coverageRepository;
        this.benefitRepository = benefitRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.premiumFrequencyRepository = premiumFrequencyRepository;
        this.sumInsuredRepository = sumInsuredRepository;
        this.commissionRepository = commissionRepository;
    }

    @Override
    public ProductResponse publish(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + productId));

        if (product.getStatus() == ProductStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Product is already published");
        }

        validateProduct(product);

        product.setStatus(ProductStatus.ACTIVE);

        return toResponse(productRepository.save(product));
    }

    private void validateProduct(Product product) {

        Long productId = product.getId();

        // 1. Category must exist and be active.
        if (product.getCategory() == null
                || !product.getCategory().isActive()) {
            throw new IllegalStateException(
                    "An active product category is required");
        }

        // 2. Required product master fields.
        if (blank(product.getProductCode())
                || blank(product.getProductName())
                || blank(product.getProductType())
                || product.getMinAge() == null
                || product.getMaxAge() == null
                || product.getMinSumInsured() == null
                || product.getMaxSumInsured() == null
                || product.getMinPolicyTerm() == null
                || product.getMaxPolicyTerm() == null
                || blank(product.getPremiumFrequency())) {

            throw new IllegalStateException(
                    "Complete all required product fields before publishing");
        }

        // 3. Age and policy term validations.
        if (product.getMinAge() < 0
                || product.getMaxAge() > 120
                || product.getMinAge() > product.getMaxAge()
                || product.getMinPolicyTerm() <= 0
                || product.getMinPolicyTerm()
                > product.getMaxPolicyTerm()) {

            throw new IllegalStateException(
                    "Invalid product age limits or policy term");
        }

        // 4. Product sum-insured limits.
        if (product.getMinSumInsured()
                .compareTo(BigDecimal.ZERO) <= 0
                || product.getMinSumInsured()
                .compareTo(product.getMaxSumInsured()) > 0) {

            throw new IllegalStateException(
                    "Invalid product sum-insured limits");
        }

        // 5. At least one active coverage.
        if (coverageRepository
                .findByProduct_IdAndActiveTrue(productId).isEmpty()) {

            throw new IllegalStateException(
                    "Add at least one active coverage before publishing");
        }

        // 6. At least one active benefit.
        if (benefitRepository
                .findByProduct_IdAndActiveTrue(productId).isEmpty()) {

            throw new IllegalStateException(
                    "Add at least one active benefit before publishing");
        }

        // 7. Eligibility rules must exist and be valid.
        var eligibility = eligibilityRuleRepository
                .findByProduct_Id(productId)
                .orElseThrow(() -> new IllegalStateException(
                        "Eligibility rules are required before publishing"));

        if (eligibility.getMinimumAge() == null
                || eligibility.getMaximumAge() == null
                || eligibility.getMinimumIncome() == null
                || eligibility.getMinimumAge() < 0
                || eligibility.getMaximumAge() > 120
                || eligibility.getMinimumAge()
                > eligibility.getMaximumAge()
                || eligibility.getMinimumIncome()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalStateException(
                    "Eligibility rules contain invalid values");
        }

        if (eligibility.getMinimumAge() < product.getMinAge()
                || eligibility.getMaximumAge() > product.getMaxAge()) {

            throw new IllegalStateException(
                    "Eligibility age limits must be within product age limits");
        }

        // 8. At least one valid active sum-insured configuration.
        List<SumInsuredConfiguration> configurations =
                sumInsuredRepository.findByProduct_Id(productId)
                        .stream()
                        .filter(SumInsuredConfiguration::isActive)
                        .toList();

        if (configurations.isEmpty()) {
            throw new IllegalStateException(
                    "An active sum-insured configuration is required");
        }

        for (SumInsuredConfiguration config : configurations) {

            BigDecimal minimum = config.getMinimumSumInsured();
            BigDecimal maximum = config.getMaximumSumInsured();
            BigDecimal defaultAmount = config.getDefaultSumInsured();

            if (minimum == null
                    || maximum == null
                    || minimum.compareTo(BigDecimal.ZERO) <= 0
                    || minimum.compareTo(maximum) > 0
                    || minimum.compareTo(product.getMinSumInsured()) < 0
                    || maximum.compareTo(product.getMaxSumInsured()) > 0) {

                throw new IllegalStateException(
                        "Sum-insured configuration must be valid and within product limits");
            }

            if (defaultAmount != null
                    && (defaultAmount.compareTo(minimum) < 0
                    || defaultAmount.compareTo(maximum) > 0)) {

                throw new IllegalStateException(
                        "Default sum insured must be within configured limits");
            }
        }

        // 9. Selected premium frequency must be active.
        boolean frequencyExists =
                premiumFrequencyRepository.findByProduct_Id(productId)
                        .stream()
                        .anyMatch(frequency ->
                                frequency.isActive()
                                        && frequency.getFrequency() != null
                                        && frequency.getFrequency()
                                        .equalsIgnoreCase(
                                                product.getPremiumFrequency()));

        if (!frequencyExists) {
            throw new IllegalStateException(
                    "Configure the selected premium frequency and activate it");
        }

        // 10. An active commission configuration is required.
        var commission = commissionRepository
                .findByProductCategory_Id(
                        product.getCategory().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Commission configuration is required before publishing"));

        if (!commission.isActive()) {
            throw new IllegalStateException(
                    "Commission configuration must be active");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
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
