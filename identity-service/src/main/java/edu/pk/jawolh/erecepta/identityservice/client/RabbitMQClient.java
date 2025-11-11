package edu.pk.jawolh.erecepta.identityservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQClient {

    public void sendVerificationCodeEvent(String email, String verificationCode) {}

    public void sendResetPasswordCodeEvent(String email, String resetPasswordCode) {}
}
