package edu.pk.jawolh.erecepta.doc_gen.consumer;

import edu.pk.jawolh.erecepta.common.prescription.messeges.GeneratePrescriptionMessage;
import edu.pk.jawolh.erecepta.common.prescription.messeges.PrescriptionEmailMessage;
import edu.pk.jawolh.erecepta.doc_gen.model.PrescriptionPdfData;
import edu.pk.jawolh.erecepta.doc_gen.rabbitMq.RabbitMqMessageSender;
import edu.pk.jawolh.erecepta.doc_gen.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final PdfGeneratorService pdfGeneratorService;
    private final RabbitMqMessageSender rabbitMqMessageSender;

    @RabbitListener(queues = "#{@rabbitMqProperties.getGeneratePrescriptionEventTopic()}")
    public void receiveGeneratePrescriptionMessage(GeneratePrescriptionMessage message) {
        log.info("Received request to generate PDF for Prescription ID: {}", message.getPrescriptionId());

        try {
            PrescriptionPdfData pdfData = mapToPdfData(message);

            byte[] pdfContent = pdfGeneratorService.generatePrescriptionPdf(pdfData);

            log.info("PDF generated successfully. Size: {} bytes", pdfContent.length);

            PrescriptionEmailMessage emailMessage = PrescriptionEmailMessage.builder()
                    .recipientEmail(message.getPatientEmail())
                    .patientName(message.getPatientName())
                    .prescriptionId(message.getPrescriptionId())
                    .pdfContent(pdfContent)
                    .build();

            rabbitMqMessageSender.sendPrescriptionEmail(emailMessage);

        } catch (Exception e) {
            log.error("Failed to process prescription ID: {}", message.getPrescriptionId(), e);
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
