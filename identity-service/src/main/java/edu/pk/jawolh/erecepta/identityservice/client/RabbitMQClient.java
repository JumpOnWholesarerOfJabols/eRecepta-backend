package edu.pk.jawolh.erecepta.identityservice.client;

import edu.pk.jawolh.erecepta.common.user.messages.ResetPasswordCodeMessage;
import edu.pk.jawolh.erecepta.common.user.messages.VerificationCodeMessage;
import edu.pk.jawolh.erecepta.identityservice.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQClient {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public void sendVerificationCodeEvent(String email, String verificationCode) {
        VerificationCodeMessage event = new VerificationCodeMessage(email, verificationCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getVerificationCodeRoutingKey(),
                event
        );
    }

    public void sendResetPasswordCodeEvent(String email, String resetPasswordCode) {
        ResetPasswordCodeMessage event = new ResetPasswordCodeMessage(email, resetPasswordCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getResetPasswordCodeRoutingKey(),
                event
        );
    }
}
