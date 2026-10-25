
package com.insurance.quote.dto;

import java.math.BigDecimal;

public class PremiumCalculationResponseDto {

    private Long calculationId;
    private Long quoteId;
    private BigDecimal basePremium;
    private BigDecimal riskLoading;
    private BigDecimal riderPremium;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
    private BigDecimal finalPremium;

    public PremiumCalculationResponseDto() {
    }

    public Long getCalculationId() {
        return calculationId;
    }

    public void setCalculationId(Long calculationId) {
        this.calculationId = calculationId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public BigDecimal getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(BigDecimal basePremium) {
        this.basePremium = basePremium;
    }

    public BigDecimal getRiskLoading() {
        return riskLoading;
    }

    public void setRiskLoading(BigDecimal riskLoading) {
        this.riskLoading = riskLoading;
    }

    public BigDecimal getRiderPremium() {
        return riderPremium;
    }

    public void setRiderPremium(BigDecimal riderPremium) {
        this.riderPremium = riderPremium;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getFinalPremium() {
        return finalPremium;
    }

    public void setFinalPremium(BigDecimal finalPremium) {
        this.finalPremium = finalPremium;
    }
}
