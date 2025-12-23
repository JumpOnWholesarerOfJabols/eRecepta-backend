package edu.pk.jawolh.erecepta.doc_gen.rabbitMq;

import edu.pk.jawolh.erecepta.common.prescription.messeges.PrescriptionEmailMessage;
import edu.pk.jawolh.erecepta.doc_gen.configuration.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqMessageSender {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public void sendPrescriptionEmail(PrescriptionEmailMessage message) {
        try {
            log.info("Sending prescription email event for user: {}", message.getRecipientEmail());

            rabbitTemplate.convertAndSend(
                    rabbitMqProperties.getExchangeName(),
                    rabbitMqProperties.getPrescriptionEmailRoutingKey(),
                    message
            );

            log.info("Prescription email event sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send prescription email event for user: {}", message.getRecipientEmail(), e);
        }
    }
}
