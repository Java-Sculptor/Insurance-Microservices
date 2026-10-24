
package com.insurance.product.service;

import com.insurance.product.dto.CommissionConfigurationRequest;
import com.insurance.product.dto.CommissionConfigurationResponse;

public interface CommissionConfigurationService {

    CommissionConfigurationResponse create(
            Long categoryId,
            CommissionConfigurationRequest request);

    CommissionConfigurationResponse getByCategory(Long categoryId);

    CommissionConfigurationResponse update(
            Long categoryId,
            CommissionConfigurationRequest request);

    void delete(Long categoryId);
}
