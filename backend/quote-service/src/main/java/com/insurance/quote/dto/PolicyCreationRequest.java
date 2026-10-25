package com.insurance.quote.dto;

import java.time.LocalDate;

public class PolicyCreationRequest {

    private Long quoteId;
    private Long customerId;
    private Long productId;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;

    public PolicyCreationRequest() {
    }

    public PolicyCreationRequest(Long quoteId, Long customerId,
                                 Long productId, LocalDate effectiveDate,
                                 LocalDate expiryDate) {
        this.quoteId = quoteId;
        this.customerId = customerId;
        this.productId = productId;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
    }

    public Long getQuoteId() { return quoteId; }
    public Long getCustomerId() { return customerId; }
    public Long getProductId() { return productId; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
}