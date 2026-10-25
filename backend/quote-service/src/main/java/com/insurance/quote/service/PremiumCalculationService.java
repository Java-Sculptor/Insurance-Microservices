
package com.insurance.quote.service;


import com.insurance.quote.dto.PremiumCalculationRequestDto;
import com.insurance.quote.dto.PremiumCalculationResponseDto;

public interface PremiumCalculationService {

    PremiumCalculationResponseDto calculatePremium(
            PremiumCalculationRequestDto requestDto);

    PremiumCalculationResponseDto getPremiumByQuoteId(Long quoteId);
}
