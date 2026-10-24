
package com.insurance.product.service;

import com.insurance.product.dto.SumInsuredConfigurationRequest;
import com.insurance.product.dto.SumInsuredConfigurationResponse;

import java.util.List;

public interface SumInsuredConfigurationService {

    SumInsuredConfigurationResponse create(
            Long productId,
            SumInsuredConfigurationRequest request);

    List<SumInsuredConfigurationResponse> getByProduct(Long productId);

    SumInsuredConfigurationResponse update(
            Long productId,
            Long configurationId,
            SumInsuredConfigurationRequest request);

    void delete(Long productId, Long configurationId);
}
