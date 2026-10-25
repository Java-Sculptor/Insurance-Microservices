package com.insurance.quote.dto;

import com.insurance.quote.enums.RiskCategory;

public class RiskAssessmentResponseDto {

    private Long riskId;
    private Long quoteId;
    private Integer ageScore;
    private Integer occupationScore;
    private Integer medicalScore;
    private Integer lifestyleScore;
    private Integer totalScore;
    private RiskCategory riskCategory;

    public RiskAssessmentResponseDto() {
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
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
