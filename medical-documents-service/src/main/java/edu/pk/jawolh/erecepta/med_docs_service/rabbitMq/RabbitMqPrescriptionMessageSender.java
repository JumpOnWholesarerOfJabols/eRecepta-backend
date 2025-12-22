package edu.pk.jawolh.erecepta.med_docs_service.rabbitMq;

import edu.pk.jawolh.erecepta.common.prescription.messeges.GeneratePrescriptionMessage;
import edu.pk.jawolh.erecepta.med_docs_service.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqPrescriptionMessageSender {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public void sendPrescriptionGenerationRequest(GeneratePrescriptionMessage message) {
        try {
            log.info("Sending prescription generation request for ID: {}", message.getPrescriptionId());

            rabbitTemplate.convertAndSend(
                    rabbitMqProperties.getExchangeName(),
                    rabbitMqProperties.getGeneratePrescriptionEventRoutingKey(),
                    message
            );

            log.info("Message sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ", e);
        }
    }
}
