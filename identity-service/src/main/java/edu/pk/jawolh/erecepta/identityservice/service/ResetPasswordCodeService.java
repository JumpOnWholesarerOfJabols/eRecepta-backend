package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.identityservice.config.CodeProperties;
import edu.pk.jawolh.erecepta.identityservice.exception.CodeDoesNotExistException;
import edu.pk.jawolh.erecepta.identityservice.exception.CodeExpiredException;
import edu.pk.jawolh.erecepta.identityservice.exception.InvalidCredentialsException;
import edu.pk.jawolh.erecepta.identityservice.model.ResetPasswordCode;
import edu.pk.jawolh.erecepta.identityservice.model.UserVerificationCode;
import edu.pk.jawolh.erecepta.identityservice.repository.ResetPasswordCodeRepository;
import edu.pk.jawolh.erecepta.identityservice.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordCodeService {
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;
    private final CodeGenerator codeGenerator;
    private final CodeProperties codeProperties;

    @Transactional
    public String generateResetPasswordCode(UUID userId) {
        if (resetPasswordCodeRepository.existsByUserId(userId))
            resetPasswordCodeRepository.deleteAllByUserId(userId);

        String code = codeGenerator.generateCode(codeProperties.getLength());
        ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
                .userId(userId)
                .code(code)
                .expiryDate(LocalDateTime.now().plus(codeProperties.getExpiration()))
                .build();

        resetPasswordCodeRepository.save(resetPasswordCode);

        return code;
    }

    @Transactional
    public void verifyResetPasswordCode(UUID userId, String code) {
        ResetPasswordCode resetPasswordCode =
                resetPasswordCodeRepository
                        .findByUserId(userId)
                        .orElseThrow(()->
                                new CodeDoesNotExistException("Reset password code not found for provided PESEL or email"));

        if (!resetPasswordCode.getCode().equals(code)) {
            throw new InvalidCredentialsException("Invalid reset password code");
        }

        if (resetPasswordCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new CodeExpiredException("Expired reset password code");
        }

        resetPasswordCodeRepository.delete(resetPasswordCode);
    }
}
