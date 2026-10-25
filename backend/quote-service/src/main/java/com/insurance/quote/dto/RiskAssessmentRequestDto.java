
package com.insurance.quote.dto;

public class RiskAssessmentRequestDto {

    private Long quoteId;

    private Integer ageScore;
    private Integer occupationScore;
    private Integer medicalScore;
    private Integer lifestyleScore;

    public RiskAssessmentRequestDto() {
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
}
