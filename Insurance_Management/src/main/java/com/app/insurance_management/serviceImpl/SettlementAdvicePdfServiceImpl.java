
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.entity.ClaimSettlement;
import com.app.insurance_management.service.SettlementAdvicePdfService;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class SettlementAdvicePdfServiceImpl
        implements SettlementAdvicePdfService {

    private static final String DIRECTORY =
            "uploads/settlement-advice";

    @Override
    public String generateSettlementAdvice(
            ClaimSettlement settlement) {

        File directory = new File(DIRECTORY);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException(
                    "Unable to create settlement advice directory");
        }

        String filePath = DIRECTORY
                + "/settlement-"
                + settlement.getSettlementId()
                + ".pdf";

        Document document = new Document();

        try {
            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(filePath));

            document.open();

            document.add(new Paragraph("SETTLEMENT ADVICE"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Settlement ID: "
                            + settlement.getSettlementId()));

            document.add(new Paragraph(
                    "Claim ID: "
                            + settlement.getClaim().getClaimId()));

            document.add(new Paragraph(
                    "Claim Number: "
                            + settlement.getClaim().getClaimNumber()));

            document.add(new Paragraph(
                    "Payment Mode: "
                            + settlement.getPaymentMode()));

            document.add(new Paragraph(
                    "Transaction Reference: "
                            + (settlement.getTransactionReference() == null
                            ? "Not available"
                            : settlement.getTransactionReference())));

            document.add(new Paragraph(
                    "Settlement Date: "
                            + (settlement.getSettlementDate() == null
                            ? "Not available"
                            : settlement.getSettlementDate().format(
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy HH:mm")))));

            document.add(new Paragraph(
                    "Settlement Status: "
                            + settlement.getSettlementStatus()));

            document.add(new Paragraph(
                    "Remarks: "
                            + (settlement.getRemarks() == null
                            ? "N/A"
                            : settlement.getRemarks())));

            document.close();

            return filePath;

        } catch (Exception e) {
            if (document.isOpen()) {
                document.close();
            }

            throw new RuntimeException(
                    "Failed to generate settlement advice PDF", e);
        }
    }
}
