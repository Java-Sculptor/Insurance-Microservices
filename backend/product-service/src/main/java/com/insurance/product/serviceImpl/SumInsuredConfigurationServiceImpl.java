
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.SumInsuredConfigurationRequest;
import com.insurance.product.dto.SumInsuredConfigurationResponse;
import com.insurance.product.entity.Product;
import com.insurance.product.entity.SumInsuredConfiguration;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.repository.SumInsuredConfigurationRepository;
import com.insurance.product.service.SumInsuredConfigurationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SumInsuredConfigurationServiceImpl
        implements SumInsuredConfigurationService {

    private final SumInsuredConfigurationRepository configurationRepository;
    private final ProductRepository productRepository;

    public SumInsuredConfigurationServiceImpl(
            SumInsuredConfigurationRepository configurationRepository,
            ProductRepository productRepository) {
        this.configurationRepository = configurationRepository;
        this.productRepository = productRepository;
    }

    @Override
    public SumInsuredConfigurationResponse create(
            Long productId,
            SumInsuredConfigurationRequest request) {

        validateRequest(request);
        Product product = findProduct(productId);

        if (configurationRepository.existsByProduct_Id(productId)) {
            throw new IllegalArgumentException(
                    "Sum insured configuration already exists for product: "
                            + productId);
        }

        SumInsuredConfiguration configuration =
                new SumInsuredConfiguration();

        configuration.setProduct(product);
        applyRequest(configuration, request);

        return toResponse(
                configurationRepository.save(configuration));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SumInsuredConfigurationResponse> getByProduct(
            Long productId) {

        findProduct(productId);

        return configurationRepository.findByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SumInsuredConfigurationResponse update(
            Long productId,
            Long configurationId,
            SumInsuredConfigurationRequest request) {

        validateRequest(request);

        SumInsuredConfiguration configuration =
                configurationRepository
                        .findByIdAndProduct_Id(configurationId, productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sum insured configuration not found: "
                                                + configurationId));

        applyRequest(configuration, request);

        return toResponse(
                configurationRepository.save(configuration));
    }

    @Override
    public void delete(Long productId, Long configurationId) {

        SumInsuredConfiguration configuration =
                configurationRepository
                        .findByIdAndProduct_Id(configurationId, productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sum insured configuration not found: "
                                                + configurationId));

        configurationRepository.delete(configuration);
    }

    private void validateRequest(
            SumInsuredConfigurationRequest request) {

        if (request.maximumSumInsured()
                .compareTo(request.minimumSumInsured()) < 0) {
            throw new IllegalArgumentException(
                    "Maximum sum insured must be greater than or equal to minimum sum insured.");
        }

        if (request.defaultSumInsured() != null) {

            if (request.defaultSumInsured()
                    .compareTo(request.minimumSumInsured()) < 0
                    || request.defaultSumInsured()
                    .compareTo(request.maximumSumInsured()) > 0) {

                throw new IllegalArgumentException(
                        "Default sum insured must be between minimum and maximum sum insured.");
            }
        }
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + productId));
    }

    private void applyRequest(
            SumInsuredConfiguration configuration,
            SumInsuredConfigurationRequest request) {

        configuration.setMinimumSumInsured(
                request.minimumSumInsured());

        configuration.setMaximumSumInsured(
                request.maximumSumInsured());

        configuration.setDefaultSumInsured(
                request.defaultSumInsured());
    }

    private SumInsuredConfigurationResponse toResponse(
            SumInsuredConfiguration configuration) {

        return new SumInsuredConfigurationResponse(
                configuration.getId(),
                configuration.getProduct().getId(),
                configuration.getMinimumSumInsured(),
                configuration.getMaximumSumInsured(),
                configuration.getDefaultSumInsured(),
                configuration.isActive(),
                configuration.getCreatedAt(),
                configuration.getUpdatedAt()
        );
    }
}
