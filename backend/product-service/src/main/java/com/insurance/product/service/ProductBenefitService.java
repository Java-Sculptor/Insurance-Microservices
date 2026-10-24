
package com.insurance.product.service;

import com.insurance.product.dto.BenefitRequest;
import com.insurance.product.dto.BenefitResponse;

import java.util.List;

public interface ProductBenefitService {

    BenefitResponse create(Long productId, BenefitRequest request);

    List<BenefitResponse> getByProduct(Long productId);

    BenefitResponse update(Long benefitId, BenefitRequest request);

    void delete(Long benefitId);
}
