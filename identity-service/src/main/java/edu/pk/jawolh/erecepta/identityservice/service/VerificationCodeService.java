package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.identityservice.config.CodeProperties;
import edu.pk.jawolh.erecepta.identityservice.model.UserVerificationCode;
import edu.pk.jawolh.erecepta.identityservice.repository.VerificationCodeRepository;
import edu.pk.jawolh.erecepta.identityservice.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final CodeProperties codeProperties;
    private final CodeGenerator codeGenerator;

    @Transactional
    public String generateVerificationCode(String email, String pesel) {
        if (verificationCodeRepository.existsByPeselOrEmail(email, pesel))
            verificationCodeRepository.deleteAllByPeselOrEmail(email, pesel);

        String code = codeGenerator.generateCode(codeProperties.getLength());
        UserVerificationCode userVerificationCode = UserVerificationCode.builder()
                .pesel(pesel)
                .email(email)
                .code(code)
                .expiryDate(LocalDateTime.now().plus(codeProperties.getExpiration()))
                .build();

        verificationCodeRepository.save(userVerificationCode);

        return code;
    }

    @Transactional
    public void verifyVerificationCode(String email, String pesel, String code) {
        UserVerificationCode verificationCode =
                verificationCodeRepository
                        .findByPeselOrEmail(email, pesel)
                        .orElseThrow(()->
                                new IllegalArgumentException("Verification code not found for provided PESEL or email"));

        if (!verificationCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Expired verification code");
        }

        verificationCodeRepository.delete(verificationCode);
    }
}
