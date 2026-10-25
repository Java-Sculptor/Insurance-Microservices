package com.insurance.quote.dto;


public class PremiumCalculationRequestDto {

    private Long quoteId;

    public PremiumCalculationRequestDto() {
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }
}
