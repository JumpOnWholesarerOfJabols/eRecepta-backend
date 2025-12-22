package edu.pk.jawolh.erecepta.doc_gen.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {

    private String exchangeName;

    private String generatePrescriptionEventRoutingKey;
    private String generatePrescriptionEventTopic;
}