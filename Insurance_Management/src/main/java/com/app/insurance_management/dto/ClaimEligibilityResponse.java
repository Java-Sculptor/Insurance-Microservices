package com.app.insurance_management.dto;

import java.util.List;

public record ClaimEligibilityResponse(
        boolean eligible,
        List<String> errors
) {}