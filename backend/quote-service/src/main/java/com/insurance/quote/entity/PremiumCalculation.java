
package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "premium_calculation")
public class PremiumCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calculation_id")
    private Long calculationId;

    @ManyToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "base_premium", precision = 15, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "risk_loading", precision = 15, scale = 2)
    private BigDecimal riskLoading;

    @Column(name = "rider_premium", precision = 15, scale = 2)
    private BigDecimal riderPremium;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "gst_amount", precision = 15, scale = 2)
    private BigDecimal gstAmount;

    @Column(name = "final_premium", precision = 15, scale = 2)
    private BigDecimal finalPremium;

    public PremiumCalculation() {
    }

    public Long getCalculationId() {
        return calculationId;
    }

    public void setCalculationId(Long calculationId) {
        this.calculationId = calculationId;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
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
