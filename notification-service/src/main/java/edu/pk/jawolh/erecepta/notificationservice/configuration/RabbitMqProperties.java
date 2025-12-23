package edu.pk.jawolh.erecepta.notificationservice.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {
    private String exchangeName;
    private String prescriptionExchangeName;

    private String resetPasswordCodeEventTopic;
    private String verificationCodeEventTopic;
    private String visitChangeEventTopic;
    private String prescriptionEmailEventTopic;

    private String resetPasswordCodeEventRoutingKey;
    private String verificationCodeEventRoutingKey;
    private String visitChangeRoutingKey;
    private String prescriptionEmailEventRoutingKey;

}
