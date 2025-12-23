package edu.pk.jawolh.erecepta.doc_gen.test;

import edu.pk.jawolh.erecepta.doc_gen.model.PrescriptionPdfData;
import edu.pk.jawolh.erecepta.doc_gen.service.PdfGeneratorService;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
public class PdfGeneratorTest {

    public static void main(String[] args) {
        log.info(">>> Starting");

        try {

            String prescriptionId = UUID.randomUUID().toString();
            String accessCode = "9972";
            Integer totalPackages = 3;
            String status = "ISSUED";
            String issueDate = "2023-11-20";
            String expiresAt = "2023-12-20";

            String fetchedPatientName = "John Doe";
            String fetchedPatientPesel = "90050512345";
            String fetchedDoctorName = "Dr. Gregory House";
            String fetchedMedicationName = "Amoxicillin 875mg";

            PrescriptionPdfData pdfDto = PrescriptionPdfData.builder()
                    .prescriptionId(prescriptionId)
                    .accessCode(accessCode)
                    .status(status)
                    .totalPackages(totalPackages)
                    .issueDate(issueDate)
                    .expirationDate(expiresAt)
                    .patientName(fetchedPatientName)
                    .patientPesel(fetchedPatientPesel)
                    .doctorName(fetchedDoctorName)
                    .medicationName(fetchedMedicationName)
                    .build();

            PdfGeneratorService generatorService = new PdfGeneratorService();
            byte[] pdfBytes = generatorService.generatePrescriptionPdf(pdfDto);

            String fileName = UUID.randomUUID() + ".pdf";
            try (OutputStream os = new FileOutputStream(fileName)) {
                os.write(pdfBytes);
            }

            log.info(">>> SUCCESS! PDF generated: {}", fileName);

        } catch (Exception e) {
            log.error(">>> ERROR during PDF generation:", e);
        }
    }
}