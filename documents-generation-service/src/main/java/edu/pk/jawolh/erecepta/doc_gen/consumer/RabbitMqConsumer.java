package edu.pk.jawolh.erecepta.doc_gen.consumer;

import edu.pk.jawolh.erecepta.common.prescription.messeges.GeneratePrescriptionMessage;
import edu.pk.jawolh.erecepta.common.user.messages.ResetPasswordCodeMessage;
import edu.pk.jawolh.erecepta.common.user.messages.VerificationCodeMessage;
import edu.pk.jawolh.erecepta.common.visit.messages.VisitMessage;
import edu.pk.jawolh.erecepta.doc_gen.model.PrescriptionPdfData;
import edu.pk.jawolh.erecepta.doc_gen.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    PdfGeneratorService pdfGeneratorService;

    @RabbitListener(queues = "#{@rabbitMqProperties.getGeneratePrescriptionEventTopic()}")
    public void receiveGeneratePrescriptionMessage(GeneratePrescriptionMessage message) {
        log.info("Received request to generate PDF for Prescription ID: {}", message.getPrescriptionId());

        try {
            PrescriptionPdfData pdfData = mapToPdfData(message);

            byte[] pdfContent = pdfGeneratorService.generatePrescriptionPdf(pdfData);

            log.info("PDF generated successfully. Size: {} bytes", pdfContent.length);

            String filename = UUID.randomUUID() + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                fos.write(pdfContent);
                log.info(">>> [DEBUG] PDF saved locally to: {}", filename);
            } catch (IOException ioException) {
                log.error("Could not save temporary PDF file", ioException);
            }


        } catch (Exception e) {
            log.error("Failed to generate PDF for Prescription ID: {}", message.getPrescriptionId(), e);
        }
    }

    private PrescriptionPdfData mapToPdfData(GeneratePrescriptionMessage msg) {
        return PrescriptionPdfData.builder()
                .prescriptionId(msg.getPrescriptionId())
                .accessCode(msg.getAccessCode())
                .totalPackages(msg.getTotalPackages())
                .issueDate(msg.getIssueDate())
                .expirationDate(msg.getExpirationDate())
                .status(msg.getStatus())
                .patientName(msg.getPatientName())
                .patientPesel(msg.getPatientPesel())
                .doctorName(msg.getDoctorName())
                .medicationName(msg.getMedicationName())
                .build();
    }
}
