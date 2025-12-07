package edu.pk.jawolh.erecepta.notificationservice.consumer;

import edu.pk.jawolh.erecepta.common.user.messages.ResetPasswordCodeMessage;
import edu.pk.jawolh.erecepta.common.user.messages.VerificationCodeMessage;
import edu.pk.jawolh.erecepta.common.visit.messages.VisitMessage;
import edu.pk.jawolh.erecepta.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    private final EmailService emailService;

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
}
