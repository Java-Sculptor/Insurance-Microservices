
package com.insurance.quote.entity;


import com.insurance.quote.enums.RiskCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_assessment")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_id")
    private Long riskId;

    @ManyToOne
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "age_score")
    private Integer ageScore;

    @Column(name = "occupation_score")
    private Integer occupationScore;

    @Column(name = "medical_score")
    private Integer medicalScore;

    @Column(name = "lifestyle_score")
    private Integer lifestyleScore;

    @Column(name = "total_score")
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category")
    private RiskCategory riskCategory;

    public RiskAssessment() {
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Integer getAgeScore() {
        return ageScore;
    }

    public void setAgeScore(Integer ageScore) {
        this.ageScore = ageScore;
    }

    public Integer getOccupationScore() {
        return occupationScore;
    }

    public void setOccupationScore(Integer occupationScore) {
        this.occupationScore = occupationScore;
    }

    public Integer getMedicalScore() {
        return medicalScore;
    }

    public void setMedicalScore(Integer medicalScore) {
        this.medicalScore = medicalScore;
    }

    public Integer getLifestyleScore() {
        return lifestyleScore;
    }

    public void setLifestyleScore(Integer lifestyleScore) {
        this.lifestyleScore = lifestyleScore;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }
}
