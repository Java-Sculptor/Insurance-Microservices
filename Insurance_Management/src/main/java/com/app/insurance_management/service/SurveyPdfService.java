package com.app.insurance_management.service;

import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.SurveyAssignment;

public interface SurveyPdfService {

    String generateSurveyPdf(
            ClaimMaster claim,
            SurveyAssignment assignment
    );
}