package edu.pk.jawolh.erecepta.doc_gen.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMqConfig {

    private final RabbitMqProperties rabbitMqProperties;


    @Bean
    public Queue generatePrescriptionEventQueue() {
        return new Queue(rabbitMqProperties.getGeneratePrescriptionEventTopic());
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(rabbitMqProperties.getExchangeName());
    }

    @Bean
    public Binding generatePrescriptionEventBinding(
            @Qualifier("generatePrescriptionEventQueue") Queue queue,
            TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(rabbitMqProperties.getGeneratePrescriptionEventRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}