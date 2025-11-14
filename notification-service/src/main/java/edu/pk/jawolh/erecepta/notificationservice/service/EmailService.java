package edu.pk.jawolh.erecepta.notificationservice.service;

import edu.pk.jawolh.erecepta.notificationservice.configuration.EmailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final MessageSource messageSource;

    @Async
    public void sendVerificationCode(String to, String code) {
        String subject = messageSource.getMessage(
                "email.verification.subject", null, Locale.getDefault()
        );
        String body = messageSource.getMessage(
                "email.verification.body", new Object[]{code}, Locale.getDefault()
        );

        sendEmail(to, subject, body);
    }

    @Async
    public void sendRestPasswordCode(String to, String code) {
        String subject = messageSource.getMessage(
                "email.reset-password.subject", null, Locale.getDefault()
        );
        String body = messageSource.getMessage(
                "email.reset-password.body", new Object[]{code}, Locale.getDefault()
        );

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperties.getSenderAddress());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
