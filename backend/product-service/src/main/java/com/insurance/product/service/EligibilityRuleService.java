
package com.insurance.product.service;

import com.insurance.product.dto.EligibilityRuleRequest;
import com.insurance.product.dto.EligibilityRuleResponse;

public interface EligibilityRuleService {

    EligibilityRuleResponse create(
            Long productId,
            EligibilityRuleRequest request);

    EligibilityRuleResponse getByProduct(Long productId);

    EligibilityRuleResponse update(
            Long productId,
            EligibilityRuleRequest request);

    void delete(Long productId);
}
