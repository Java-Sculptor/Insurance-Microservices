
package com.insurance.quote.dto;

import java.math.BigDecimal;

public class QuoteRequestDto {

    private Long customerId;
    private Long productId;
    private Integer policyTerm;
    private BigDecimal sumInsured;
    private Long premiumFrequencyId;

    public QuoteRequestDto() {
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
}
