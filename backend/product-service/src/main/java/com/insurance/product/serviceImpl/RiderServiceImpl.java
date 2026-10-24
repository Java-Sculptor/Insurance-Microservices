
package com.insurance.product.serviceImpl;

import com.insurance.product.dto.RiderRequest;
import com.insurance.product.dto.RiderResponse;
import com.insurance.product.entity.Product;
import com.insurance.product.entity.Rider;
import com.insurance.product.exception.DuplicateResourceException;
import com.insurance.product.exception.ResourceNotFoundException;
import com.insurance.product.repository.ProductRepository;
import com.insurance.product.repository.RiderRepository;
import com.insurance.product.service.RiderService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RiderServiceImpl implements RiderService {

    private final RiderRepository riderRepository;
    private final ProductRepository productRepository;

    public RiderServiceImpl(
            RiderRepository riderRepository,
            ProductRepository productRepository) {
        this.riderRepository = riderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public RiderResponse create(Long productId, RiderRequest request) {
        Product product = findProduct(productId);
        validatePremium(request.additionalPremium());

        String name = request.riderName().trim();

        if (riderRepository.existsByProduct_IdAndRiderNameIgnoreCase(
                productId, name)) {
            throw new DuplicateResourceException(
                    "Rider already exists for this product: " + name);
        }

        Rider rider = new Rider();
        rider.setProduct(product);
        applyRequest(rider, request);

        return toResponse(riderRepository.save(rider));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiderResponse> getByProduct(Long productId) {
        findProduct(productId);

        return riderRepository.findByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RiderResponse getById(Long riderId) {
        return toResponse(findRider(riderId));
    }

    @Override
    public RiderResponse update(Long riderId, RiderRequest request) {
        Rider rider = findRider(riderId);
        validatePremium(request.additionalPremium());

        String name = request.riderName().trim();

        if (riderRepository
                .existsByProduct_IdAndRiderNameIgnoreCaseAndIdNot(
                        rider.getProduct().getId(),
                        name,
                        riderId)) {
            throw new DuplicateResourceException(
                    "Rider already exists for this product: " + name);
        }

        applyRequest(rider, request);

        return toResponse(riderRepository.save(rider));
    }

    @Override
    public void delete(Long riderId) {
        Rider rider = findRider(riderId);
        rider.setActive(false);
        riderRepository.save(rider);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId));
    }

    private Rider findRider(Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rider not found: " + riderId));
    }

    private void validatePremium(BigDecimal premium) {
        if (premium == null || premium.signum() < 0) {
            throw new IllegalArgumentException(
                    "Additional premium cannot be null or negative.");
        }
    }

    private void applyRequest(Rider rider, RiderRequest request) {
        rider.setRiderName(request.riderName().trim());
        rider.setDescription(request.description());
        rider.setAdditionalPremium(request.additionalPremium());
    }

    private RiderResponse toResponse(Rider rider) {
        return new RiderResponse(
                rider.getId(),
                rider.getProduct().getId(),
                rider.getRiderName(),
                rider.getDescription(),
                rider.getAdditionalPremium(),
                rider.isActive(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
    }
}
