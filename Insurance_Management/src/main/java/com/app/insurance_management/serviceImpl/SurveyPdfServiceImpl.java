package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.SurveyAssignment;
import com.app.insurance_management.service.SurveyPdfService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SurveyPdfServiceImpl implements SurveyPdfService {

    @Value("${claim.survey.report-dir:uploads/survey-reports}")
    private String reportDirectory;

    @Override
    public String generateSurveyPdf(
            ClaimMaster claim,
            SurveyAssignment assignment) {

        Path pdfPath = null;
        Document document = new Document();

        try {
            Path directory = Paths.get(reportDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(directory);

            String fileName = "survey-claim-"
                    + claim.getClaimId()
                    + "-survey-"
                    + assignment.getSurveyId()
                    + ".pdf";

            pdfPath = directory.resolve(fileName).normalize();

            if (!pdfPath.startsWith(directory)) {
                throw new IllegalArgumentException(
                        "Invalid survey report path");
            }

            try (var outputStream = Files.newOutputStream(pdfPath)) {

                PdfWriter.getInstance(document, outputStream);
                document.open();

                Font titleFont = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD, 18);

                Font headingFont = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD, 12);

                document.add(new Paragraph(
                        "INSURANCE MANAGEMENT SYSTEM",
                        headingFont));

                document.add(new Paragraph(" "));

                document.add(new Paragraph(
                        "VEHICLE SURVEY REPORT",
                        titleFont));

                document.add(new Paragraph(" "));

                document.add(new Paragraph(
                        "Generated On: "
                                + LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                        "dd-MM-yyyy HH:mm"))));

                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1.2f, 2.8f});

                addRow(table, "Claim ID",
                        String.valueOf(claim.getClaimId()));

                addRow(table, "Claim Number",
                        claim.getClaimNumber());

                addRow(table, "Policy ID",
                        String.valueOf(claim.getPolicyId()));

                addRow(table, "Customer ID",
                        String.valueOf(claim.getCustomerId()));

                addRow(table, "Survey ID",
                        String.valueOf(assignment.getSurveyId()));

                addRow(table, "Surveyor ID",
                        String.valueOf(assignment.getSurveyorId()));

                addRow(table, "Assigned Date",
                        String.valueOf(assignment.getAssignedDate()));

                addRow(table, "Inspection Date",
                        String.valueOf(assignment.getInspectionDate()));

                addRow(table, "Claim Type",
                        String.valueOf(claim.getClaimType()));

                addRow(table, "Estimated Claim Loss",
                        money(claim.getEstimatedLoss()));

                addRow(table, "Damage Description",
                        assignment.getDamageDescription());

                addRow(table, "Assessed Damage Amount",
                        money(assignment.getAssessedDamageAmount()));

                addRow(table, "Surveyor Remarks",
                        assignment.getSurveyorRemarks());

                addRow(table, "Recommendation",
                        assignment.getRecommendation());

                addRow(table, "Survey Status",
                        String.valueOf(assignment.getSurveyStatus()));

                document.add(table);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(
                        "Surveyor Signature: ____________________"));

                document.add(new Paragraph(" "));
                document.add(new Paragraph(
                        "This report contains the surveyor's findings. "
                                + "Final claim approval and settlement are "
                                + "subject to policy terms and admin review."));

                document.close();
            }

            return pdfPath.toString();

        } catch (IOException | DocumentException ex) {

            if (document.isOpen()) {
                document.close();
            }

            if (pdfPath != null) {
                try {
                    Files.deleteIfExists(pdfPath);
                } catch (IOException ignored) {
                    // Preserve the original error
                }
            }

            throw new RuntimeException(
                    "Failed to generate survey report PDF", ex);
        }
    }

    private void addRow(
            PdfPTable table,
            String label,
            String value) {

        Font boldFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 10);

        table.addCell(new Phrase(label));
        table.addCell(new Phrase(safe(value)));
    }

    private String safe(String value) {
        return value == null || value.isBlank()
                ? "Not provided"
                : value;
    }

    private String money(BigDecimal amount) {
        return amount == null
                ? "Not provided"
                : "INR " + amount.toPlainString();
    }
}