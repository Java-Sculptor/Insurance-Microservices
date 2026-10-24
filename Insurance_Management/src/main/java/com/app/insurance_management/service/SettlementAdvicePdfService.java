
package com.app.insurance_management.service;

import com.app.insurance_management.entity.ClaimSettlement;

public interface SettlementAdvicePdfService {

    String generateSettlementAdvice(ClaimSettlement settlement);
}
