
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.CoverageRequest;
import com.insurance.product.dto.CoverageResponse;
import com.insurance.product.entity.Coverage;
import com.insurance.product.entity.Product;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.CoverageRepository;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.service.CoverageService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoverageServiceImpl implements CoverageService {

    private final CoverageRepository coverageRepository;
    private final ProductRepository productRepository;

    public CoverageServiceImpl(
            CoverageRepository coverageRepository,
            ProductRepository productRepository) {
        this.coverageRepository = coverageRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CoverageResponse create(
            Long productId, CoverageRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId));

        validateAmounts(request);

        String name = request.coverageName().trim();

        if (coverageRepository.existsByProduct_IdAndCoverageNameIgnoreCase(
                productId, name)) {
            throw new DuplicateResourceException(
                    "Coverage already exists for this product: " + name);
        }

        Coverage coverage = new Coverage();
        coverage.setProduct(product);
        applyRequest(coverage, request);

        return toResponse(coverageRepository.save(coverage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoverageResponse> getByProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found: " + productId);
        }

        return coverageRepository.findByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CoverageResponse update(
            Long coverageId, CoverageRequest request) {

        Coverage coverage = findCoverage(coverageId);
        validateAmounts(request);

        String name = request.coverageName().trim();

        if (coverageRepository
                .existsByProduct_IdAndCoverageNameIgnoreCaseAndIdNot(
                        coverage.getProduct().getId(),
                        name,
                        coverageId)) {
            throw new DuplicateResourceException(
                    "Coverage already exists for this product: " + name);
        }

        applyRequest(coverage, request);

        return toResponse(coverageRepository.save(coverage));
    }

    @Override
    public void delete(Long coverageId) {
        Coverage coverage = findCoverage(coverageId);
        coverage.setActive(false);
        coverageRepository.save(coverage);
    }

    private void validateAmounts(CoverageRequest request) {
        if (request.minimumAmount() == null
                || request.maximumAmount() == null) {
            throw new IllegalArgumentException(
                    "Minimum and maximum amounts are required.");
        }

        if (request.minimumAmount().signum() < 0
                || request.maximumAmount().signum() < 0) {
            throw new IllegalArgumentException(
                    "Coverage amounts cannot be negative.");
        }

        if (request.minimumAmount()
                .compareTo(request.maximumAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Minimum amount cannot exceed maximum amount.");
        }
    }

    private Coverage findCoverage(Long id) {
        return coverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coverage not found: " + id));
    }

    private void applyRequest(
            Coverage coverage, CoverageRequest request) {

        coverage.setCoverageName(request.coverageName().trim());
        coverage.setDescription(request.description());
        coverage.setMinimumAmount(request.minimumAmount());
        coverage.setMaximumAmount(request.maximumAmount());
    }

    private CoverageResponse toResponse(Coverage coverage) {
        return new CoverageResponse(
                coverage.getId(),
                coverage.getProduct().getId(),
                coverage.getCoverageName(),
                coverage.getDescription(),
                coverage.getMinimumAmount(),
                coverage.getMaximumAmount(),
                coverage.isActive()
        );
    }
}
