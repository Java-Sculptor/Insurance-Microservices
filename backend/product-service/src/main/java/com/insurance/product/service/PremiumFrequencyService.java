
package com.insurance.product.service;

import com.insurance.product.dto.PremiumFrequencyRequest;
import com.insurance.product.dto.PremiumFrequencyResponse;

import java.util.List;

public interface PremiumFrequencyService {

    PremiumFrequencyResponse create(
            Long productId,
            PremiumFrequencyRequest request);

    List<PremiumFrequencyResponse> getByProduct(Long productId);

    PremiumFrequencyResponse update(
            Long frequencyId,
            PremiumFrequencyRequest request);

    void delete(Long frequencyId);
}
