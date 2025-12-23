package edu.pk.jawolh.erecepta.med_docs_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {
    private String exchangeName;

    private String generatePrescriptionEventRoutingKey;
}
