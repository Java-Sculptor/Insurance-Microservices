package com.insurance.quote.dto;

import com.insurance.quote.enums.QuoteStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class QuoteResponseDto {

    private Long quoteId;
    private String quoteNumber;
    private Long customerId;
    private Long productId;
    private Integer policyTerm;
    private BigDecimal sumInsured;
    private Long premiumFrequencyId;
    private LocalDate quoteDate;
    private LocalDate expiryDate;
    private QuoteStatus quoteStatus;

    public QuoteResponseDto() {
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(Integer policyTerm) {
        this.policyTerm = policyTerm;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public Long getPremiumFrequencyId() {
        return premiumFrequencyId;
    }

    public void setPremiumFrequencyId(Long premiumFrequencyId) {
        this.premiumFrequencyId = premiumFrequencyId;
    }

    public LocalDate getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public QuoteStatus getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(QuoteStatus quoteStatus) {
        this.quoteStatus = quoteStatus;
    }
}
