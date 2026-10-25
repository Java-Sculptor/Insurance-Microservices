package com.insurance.quote.serviceImpl;


import com.insurance.quote.dto.PremiumCalculationRequestDto;
import com.insurance.quote.dto.PremiumCalculationResponseDto;
import com.insurance.quote.entity.DiscountMaster;
import com.insurance.quote.entity.PremiumCalculation;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.entity.QuoteRider;
import com.insurance.quote.repository.*;
import com.insurance.quote.service.PremiumCalculationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PremiumCalculationServiceImpl
        implements PremiumCalculationService {

    private final QuoteRepository quoteRepository;
    private final PremiumCalculationRepository premiumCalculationRepository;
    private final ProductRateRepository productRateRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final DiscountMasterRepository discountMasterRepository;
    private final QuoteRiderRepository quoteRiderRepository;
    private final ModelMapper modelMapper;

    public PremiumCalculationServiceImpl(
            QuoteRepository quoteRepository,
            PremiumCalculationRepository premiumCalculationRepository,
            ProductRateRepository productRateRepository,
            RiskAssessmentRepository riskAssessmentRepository,
            DiscountMasterRepository discountMasterRepository,
            QuoteRiderRepository quoteRiderRepository,
            ModelMapper modelMapper) {

        this.quoteRepository = quoteRepository;
        this.premiumCalculationRepository = premiumCalculationRepository;
        this.productRateRepository = productRateRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.discountMasterRepository = discountMasterRepository;
        this.quoteRiderRepository = quoteRiderRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional
    public PremiumCalculationResponseDto calculatePremium(
            PremiumCalculationRequestDto requestDto) {

        // 1. Fetch quote
        Quote quote = quoteRepository.findById(requestDto.getQuoteId())
                .orElseThrow(() ->
                        new RuntimeException("Quote not found"));

        BigDecimal sumInsured = quote.getSumInsured();

        // 2. Fetch product rate
        BigDecimal productRate =
                productRateRepository.getProductRate(
                        quote.getProductId());

        // 3. Base Premium = (Sum Insured * Product Rate) / 1000
        BigDecimal basePremium = sumInsured
                .multiply(productRate)
                .divide(new BigDecimal("1000"), 2,
                        RoundingMode.HALF_UP);

        // 4. Calculate risk loading
        BigDecimal riskPercentage =
                getRiskPercentage(quote.getQuoteId());

        BigDecimal riskLoading = basePremium
                .multiply(riskPercentage)
                .divide(new BigDecimal("100"), 2,
                        RoundingMode.HALF_UP);

        // 5. Calculate total selected rider premium
        List<QuoteRider> selectedRiders =
                quoteRiderRepository.findByQuote_QuoteId(
                        quote.getQuoteId());

        BigDecimal riderPremium = BigDecimal.ZERO;

        for (QuoteRider rider : selectedRiders) {
            if (rider.getRiderPremium() != null) {
                riderPremium = riderPremium.add(
                        rider.getRiderPremium());
            }
        }

        // 6. Calculate discount
        BigDecimal discountPercentage = getDiscountPercentage();

        BigDecimal discountAmount = basePremium
                .multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2,
                        RoundingMode.HALF_UP);

        // 7. Premium after discount and before GST
        BigDecimal premiumAfterDiscount = basePremium
                .add(riskLoading)
                .add(riderPremium)
                .subtract(discountAmount);

        if (premiumAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            premiumAfterDiscount = BigDecimal.ZERO;
        }

        // 8. GST = Premium after discount * GST percentage
        // Temporary GST rate: 18%, as shown in the BRD example
        BigDecimal gstPercentage = new BigDecimal("18");

        BigDecimal gstAmount = premiumAfterDiscount
                .multiply(gstPercentage)
                .divide(new BigDecimal("100"), 2,
                        RoundingMode.HALF_UP);

        // 9. Final Premium
        BigDecimal finalPremium = basePremium
                .add(riskLoading)
                .add(riderPremium)
                .subtract(discountAmount)
                .add(gstAmount);

        // 10. Save or update calculation for this quote
        PremiumCalculation calculation =
                premiumCalculationRepository
                        .findByQuote_QuoteId(quote.getQuoteId())
                        .orElseGet(PremiumCalculation::new);

        calculation.setQuote(quote);
        calculation.setBasePremium(basePremium);
        calculation.setRiskLoading(riskLoading);
        calculation.setRiderPremium(riderPremium);
        calculation.setDiscountAmount(discountAmount);
        calculation.setGstAmount(gstAmount);
        calculation.setFinalPremium(finalPremium);

        PremiumCalculation saved =
                premiumCalculationRepository.save(calculation);

        // 11. Map entity to response DTO manually
        // Avoids ModelMapper's ambiguous quoteId mapping
        PremiumCalculationResponseDto response =
                new PremiumCalculationResponseDto();

        response.setCalculationId(saved.getCalculationId());
        response.setQuoteId(saved.getQuote().getQuoteId());
        response.setBasePremium(saved.getBasePremium());
        response.setRiskLoading(saved.getRiskLoading());
        response.setRiderPremium(saved.getRiderPremium());
        response.setDiscountAmount(saved.getDiscountAmount());
        response.setGstAmount(saved.getGstAmount());
        response.setFinalPremium(saved.getFinalPremium());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PremiumCalculationResponseDto getPremiumByQuoteId(
            Long quoteId) {

        // 1. Fetch existing premium calculation
        PremiumCalculation calculation =
                premiumCalculationRepository
                        .findByQuote_QuoteId(quoteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Premium calculation not found for quote: "
                                                + quoteId));

        // 2. Map entity to response DTO manually
        PremiumCalculationResponseDto response =
                new PremiumCalculationResponseDto();

        response.setCalculationId(calculation.getCalculationId());
        response.setQuoteId(calculation.getQuote().getQuoteId());
        response.setBasePremium(calculation.getBasePremium());
        response.setRiskLoading(calculation.getRiskLoading());
        response.setRiderPremium(calculation.getRiderPremium());
        response.setDiscountAmount(calculation.getDiscountAmount());
        response.setGstAmount(calculation.getGstAmount());
        response.setFinalPremium(calculation.getFinalPremium());

        return response;
    }


    private BigDecimal getRiskPercentage(Long quoteId) {

        return riskAssessmentRepository
                .findByQuote_QuoteId(quoteId)
                .map(risk -> {
                    String category =
                            risk.getRiskCategory().name();

                    // Temporary values until the business rules
                    // provide the approved risk percentages.
                    switch (category.toUpperCase()) {
                        case "LOW":
                            return BigDecimal.ZERO;
                        case "MEDIUM":
                            return new BigDecimal("5");
                        case "HIGH":
                            return new BigDecimal("10");
                        default:
                            return BigDecimal.ZERO;
                    }
                })
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getDiscountPercentage() {

        LocalDate today = LocalDate.now();

        List<DiscountMaster> discounts =
                discountMasterRepository.findAll();

        for (DiscountMaster discount : discounts) {

            if (discount.getStatus() != null
                    && discount.getStatus().toString()
                    .equalsIgnoreCase("Active")
                    && discount.getValidFrom() != null
                    && discount.getValidTo() != null
                    && !today.isBefore(discount.getValidFrom())
                    && !today.isAfter(discount.getValidTo())
                    && discount.getDiscountPercentage() != null) {

                // Temporary: use the first currently valid discount.
                return discount.getDiscountPercentage();
            }
        }

        return BigDecimal.ZERO;
    }
}
