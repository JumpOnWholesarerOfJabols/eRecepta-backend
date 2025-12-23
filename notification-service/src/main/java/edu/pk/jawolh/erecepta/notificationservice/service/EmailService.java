package edu.pk.jawolh.erecepta.notificationservice.service;

import edu.pk.jawolh.erecepta.common.visit.messages.VisitMessage;
import edu.pk.jawolh.erecepta.notificationservice.configuration.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    @Async
    public void sendDoctorVisitStatusUpdate(VisitMessage msg) {
        Object[] params = new String[]{msg.doctorData().firstName(),
                msg.patientData().firstName(),
                msg.patientData().lastName(),
                msg.visitData().specialization().name(),
                msg.visitData().visitDateTime().toString(),
                msg.visitData().status().name()};

        String subject = messageSource.getMessage(
                "email.visit-status-doctor.subject", null, Locale.getDefault()
        );
        String body = messageSource.getMessage(
                "email.visit-status-doctor.body", params, Locale.getDefault()
        );

        sendEmail(msg.doctorData().email(), subject, body);
    }

    @Async
    public void sendPatientVisitStatusUpdate(VisitMessage msg) {
        Object[] params = new String[]{msg.patientData().firstName(),
                msg.doctorData().firstName(),
                msg.doctorData().lastName(),
                msg.visitData().specialization().name(),
                msg.visitData().visitDateTime().toString(),
                msg.visitData().status().name()};

        String subject = messageSource.getMessage(
                "email.visit-status-patient.subject", null, Locale.getDefault()
        );
        String body = messageSource.getMessage(
                "email.visit-status-patient.body", params, Locale.getDefault()
        );

        sendEmail(msg.patientData().email(), subject, body);
    }

    @Async
    public void sendPrescriptionEmail(String to, String patientName, String prescriptionId, byte[] pdfContent) {
        String subject = messageSource.getMessage(
                "email.prescription.subject",
                new Object[]{prescriptionId},
                Locale.getDefault()
        );

        String body = messageSource.getMessage(
                "email.prescription.body",
                new Object[]{patientName},
                Locale.getDefault()
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailProperties.getSenderAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment("Recepta_" + prescriptionId + ".pdf", new ByteArrayResource(pdfContent));

            mailSender.send(message);
            log.info("Email with prescription sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with prescription to {}", to, e);
        }
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
