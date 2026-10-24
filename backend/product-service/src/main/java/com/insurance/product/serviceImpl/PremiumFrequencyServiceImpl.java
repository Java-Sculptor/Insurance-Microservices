
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.PremiumFrequencyRequest;
import com.insurance.product.dto.PremiumFrequencyResponse;
import com.insurance.product.entity.PremiumFrequency;
import com.insurance.product.entity.Product;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.PremiumFrequencyRepository;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.service.PremiumFrequencyService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class PremiumFrequencyServiceImpl implements PremiumFrequencyService {

    private final PremiumFrequencyRepository frequencyRepository;
    private final ProductRepository productRepository;

    public PremiumFrequencyServiceImpl(
            PremiumFrequencyRepository frequencyRepository,
            ProductRepository productRepository) {
        this.frequencyRepository = frequencyRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PremiumFrequencyResponse create(
            Long productId,
            PremiumFrequencyRequest request) {

        Product product = findProduct(productId);
        String frequency = normalize(request.frequency());

        if (frequencyRepository.existsByProduct_IdAndFrequency(
                productId, frequency)) {
            throw new DuplicateResourceException(
                    "This premium frequency is already configured for the product.");
        }

        PremiumFrequency entity = new PremiumFrequency();
        entity.setProduct(product);
        entity.setFrequency(frequency);

        return toResponse(frequencyRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PremiumFrequencyResponse> getByProduct(Long productId) {

        findProduct(productId);

        return frequencyRepository.findByProduct_Id(productId)
                .stream()
                .filter(PremiumFrequency::isActive)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PremiumFrequencyResponse update(
            Long frequencyId,
            PremiumFrequencyRequest request) {

        PremiumFrequency entity = findFrequency(frequencyId);
        String frequency = normalize(request.frequency());

        boolean duplicate =
                frequencyRepository.existsByProduct_IdAndFrequency(
                        entity.getProduct().getId(), frequency);

        if (duplicate && !entity.getFrequency().equalsIgnoreCase(frequency)) {
            throw new DuplicateResourceException(
                    "This premium frequency is already configured for the product.");
        }

        entity.setFrequency(frequency);

        return toResponse(frequencyRepository.save(entity));
    }

    @Override
    public void delete(Long frequencyId) {
        PremiumFrequency entity = findFrequency(frequencyId);
        entity.setActive(false);
    }

    private String normalize(String frequency) {
        if (frequency == null || frequency.isBlank()) {
            throw new IllegalArgumentException(
                    "Premium frequency is required.");
        }

        return frequency.trim().toUpperCase(Locale.ROOT);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + productId));
    }

    private PremiumFrequency findFrequency(Long frequencyId) {
        return frequencyRepository.findById(frequencyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Premium frequency not found: " + frequencyId));
    }

    private PremiumFrequencyResponse toResponse(
            PremiumFrequency entity) {

        return new PremiumFrequencyResponse(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getFrequency(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
