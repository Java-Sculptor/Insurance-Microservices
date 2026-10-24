
package com.insurance.product.service;

import com.insurance.product.dto.RiderRequest;
import com.insurance.product.dto.RiderResponse;

import java.util.List;

public interface RiderService {

    RiderResponse create(Long productId, RiderRequest request);

    List<RiderResponse> getByProduct(Long productId);

    RiderResponse getById(Long riderId);

    RiderResponse update(Long riderId, RiderRequest request);

    void delete(Long riderId);
}
