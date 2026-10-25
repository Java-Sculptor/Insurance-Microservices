
package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "quote_rider")
public class QuoteRider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_selection_id")
    private Long riderSelectionId;

    @ManyToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(name = "rider_premium", precision = 15, scale = 2)
    private BigDecimal riderPremium;

    public QuoteRider() {
    }

    public Long getRiderSelectionId() {
        return riderSelectionId;
    }

    public void setRiderSelectionId(Long riderSelectionId) {
        this.riderSelectionId = riderSelectionId;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public BigDecimal getRiderPremium() {
        return riderPremium;
    }

    public void setRiderPremium(BigDecimal riderPremium) {
        this.riderPremium = riderPremium;
    }
}
