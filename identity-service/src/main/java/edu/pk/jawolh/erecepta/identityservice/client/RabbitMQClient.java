package edu.pk.jawolh.erecepta.identityservice.client;

import edu.pk.jawolh.erecepta.common.user.messages.SendResetPasswordCodeMessage;
import edu.pk.jawolh.erecepta.common.user.messages.SendVerificationCodeMessage;
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
        SendVerificationCodeMessage event = new SendVerificationCodeMessage(email, verificationCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getSendVerificationCodeRoutingKey(),
                event
        );
    }

    public void sendResetPasswordCodeEvent(String email, String resetPasswordCode) {
        SendResetPasswordCodeMessage event = new SendResetPasswordCodeMessage(email, resetPasswordCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getSendResetPasswordRoutingKey(),
                event
        );
    }
}
