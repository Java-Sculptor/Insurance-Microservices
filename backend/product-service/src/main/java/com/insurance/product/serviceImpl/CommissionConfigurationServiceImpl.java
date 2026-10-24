
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.CommissionConfigurationRequest;
import com.insurance.product.dto.CommissionConfigurationResponse;
import com.insurance.product.entity.CommissionConfiguration;
import com.insurance.product.entity.ProductCategory;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.CommissionConfigurationRepository;
import com.insurance.product.repository.ProductCategoryRepository;
import com.insurance.product.service.CommissionConfigurationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommissionConfigurationServiceImpl
        implements CommissionConfigurationService {

    private final CommissionConfigurationRepository commissionRepository;
    private final ProductCategoryRepository categoryRepository;

    public CommissionConfigurationServiceImpl(
            CommissionConfigurationRepository commissionRepository,
            ProductCategoryRepository categoryRepository) {
        this.commissionRepository = commissionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CommissionConfigurationResponse create(
            Long categoryId,
            CommissionConfigurationRequest request) {

        ProductCategory category = findCategory(categoryId);

        if (commissionRepository.existsByProductCategory_Id(categoryId)) {
            throw new IllegalArgumentException(
                    "Commission configuration already exists for category: "
                            + categoryId);
        }

        CommissionConfiguration configuration =
                new CommissionConfiguration();

        configuration.setProductCategory(category);
        applyRequest(configuration, request);

        return toResponse(
                commissionRepository.save(configuration));
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionConfigurationResponse getByCategory(Long categoryId) {
        return toResponse(findConfiguration(categoryId));
    }

    @Override
    public CommissionConfigurationResponse update(
            Long categoryId,
            CommissionConfigurationRequest request) {

        CommissionConfiguration configuration =
                findConfiguration(categoryId);

        applyRequest(configuration, request);

        return toResponse(
                commissionRepository.save(configuration));
    }

    @Override
    public void delete(Long categoryId) {
        CommissionConfiguration configuration =
                findConfiguration(categoryId);

        commissionRepository.delete(configuration);
    }

    private ProductCategory findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product category not found: " + categoryId));
    }

    private CommissionConfiguration findConfiguration(Long categoryId) {
        findCategory(categoryId);

        return commissionRepository
                .findByProductCategory_Id(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Commission configuration not found for category: "
                                        + categoryId));
    }

    private void applyRequest(
            CommissionConfiguration configuration,
            CommissionConfigurationRequest request) {

        configuration.setAgentCommission(request.agentCommission());
        configuration.setBranchCommission(request.branchCommission());
        configuration.setRenewalCommission(request.renewalCommission());
    }

    private CommissionConfigurationResponse toResponse(
            CommissionConfiguration configuration) {

        return new CommissionConfigurationResponse(
                configuration.getId(),
                configuration.getProductCategory().getId(),
                configuration.getProductCategory().getCategoryName(),
                configuration.getAgentCommission(),
                configuration.getBranchCommission(),
                configuration.getRenewalCommission(),
                configuration.isActive(),
                configuration.getCreatedAt(),
                configuration.getUpdatedAt()
        );
    }
}
