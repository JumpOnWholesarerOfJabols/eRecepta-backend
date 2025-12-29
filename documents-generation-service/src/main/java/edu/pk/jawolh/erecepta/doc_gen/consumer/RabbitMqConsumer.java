package edu.pk.jawolh.erecepta.doc_gen.consumer;

import edu.pk.jawolh.erecepta.common.prescription.messeges.GeneratePrescriptionMessage;
import edu.pk.jawolh.erecepta.common.prescription.messeges.PrescriptionEmailMessage;
import edu.pk.jawolh.erecepta.doc_gen.model.PrescriptionPdfData;
import edu.pk.jawolh.erecepta.doc_gen.rabbitMq.RabbitMqMessageSender;
import edu.pk.jawolh.erecepta.doc_gen.service.PdfGeneratorService;
import edu.pk.jawolh.erecepta.doc_gen.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final PdfGeneratorService pdfGeneratorService;
    private final RabbitMqMessageSender rabbitMqMessageSender;
    private final StorageService storageService;

    @RabbitListener(queues = "#{@rabbitMqProperties.getGeneratePrescriptionEventTopic()}")
    public void receiveGeneratePrescriptionMessage(GeneratePrescriptionMessage message) {
        log.info("Received request to generate PDF for Prescription ID: {}", message.getPrescriptionId());

        try {
            PrescriptionPdfData pdfData = mapToPdfData(message);
            byte[] pdfContent = pdfGeneratorService.generatePrescriptionPdf(pdfData);

            String fileKey = "prescription_" + message.getPrescriptionId() + "_" + UUID.randomUUID() + ".pdf";

            storageService.uploadFile(fileKey, pdfContent);
            log.info("PDF uploaded to MinIO with key: {}", fileKey);

            PrescriptionEmailMessage emailMessage = PrescriptionEmailMessage.builder()
                    .recipientEmail(message.getPatientEmail())
                    .patientName(message.getPatientName())
                    .prescriptionId(message.getPrescriptionId())
                    .fileKey(fileKey)
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
