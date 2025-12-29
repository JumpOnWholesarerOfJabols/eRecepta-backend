package edu.pk.jawolh.erecepta.notificationservice.consumer;

import edu.pk.jawolh.erecepta.common.user.messages.ResetPasswordCodeMessage;
import edu.pk.jawolh.erecepta.common.user.messages.VerificationCodeMessage;
import edu.pk.jawolh.erecepta.common.visit.messages.VisitMessage;
import edu.pk.jawolh.erecepta.notificationservice.service.EmailService;
import edu.pk.jawolh.erecepta.notificationservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final EmailService emailService;
    private final StorageService storageService;

    @RabbitListener(queues = "#{rabbitMqProperties.getVerificationCodeEventTopic()}")
    public void processVerificationCodeMessage(VerificationCodeMessage message) {
        log.info("Received verification code message: {}", message);

        emailService.sendVerificationCode(message.getEmail(), message.getCode());
    }

    @RabbitListener(queues = "#{rabbitMqProperties.getResetPasswordCodeEventTopic()}")
    public void processResetPasswordCodeMessage(ResetPasswordCodeMessage message) {
        log.info("Received reset password code message: {}", message);

        emailService.sendRestPasswordCode(message.getEmail(), message.getCode());
    }

    @RabbitListener(queues = "#{rabbitMqProperties.getVisitChangeEventTopic()}")
    public void processVisitStatusMessage(VisitMessage message) {
        log.info("Received visit status update message: {}", message);

        emailService.sendPatientVisitStatusUpdate(message);
        emailService.sendDoctorVisitStatusUpdate(message);
    }

    @RabbitListener(queues = "#{rabbitMqProperties.getPrescriptionEmailEventTopic()}")
    public void processPrescriptionEmail(edu.pk.jawolh.erecepta.common.prescription.messeges.PrescriptionEmailMessage message) {
        log.info("Received prescription email request for: {}", message.getRecipientEmail());

        try {
            byte[] pdfContent = storageService.downloadFile(message.getFileKey());
            log.info("Downloaded PDF from MinIO. Size: {} bytes", pdfContent.length);

            emailService.sendPrescriptionEmail(
                    message.getRecipientEmail(),
                    message.getPatientName(),
                    message.getPrescriptionId(),
                    pdfContent
            );

            log.info("Email sent successfully.");

        } catch (Exception e) {
            log.error("Failed to handle prescription email for ID: {}", message.getPrescriptionId(), e);
        }
    }
}
