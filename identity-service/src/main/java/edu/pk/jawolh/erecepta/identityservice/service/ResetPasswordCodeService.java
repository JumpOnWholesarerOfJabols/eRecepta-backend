package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.identityservice.model.ResetPasswordCode;
import edu.pk.jawolh.erecepta.identityservice.model.UserVerificationCode;
import edu.pk.jawolh.erecepta.identityservice.repository.ResetPasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordCodeService {
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    public String generateResetPasswordCode(String email, String pesel) {
        if (resetPasswordCodeRepository.existsByPeselOrEmail(email, pesel))
            resetPasswordCodeRepository.deleteAllByPeselOrEmail(email, pesel);

        // TODO
        //  -random code (replace hardcode)

        String code = "kodzik";
        ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
                .pesel(pesel)
                .email(email)
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        resetPasswordCodeRepository.save(resetPasswordCode);

        return code;
    }

    public void verifyResetPasswordCode(String email, String pesel, String code) {
        ResetPasswordCode resetPasswordCode =
                resetPasswordCodeRepository
                        .findByPeselOrEmail(email, pesel)
                        .orElseThrow(()->
                                new IllegalArgumentException("Reset password code not found for provided PESEL or email"));

        if (!resetPasswordCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid reset password code");
        }

        if (resetPasswordCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Expired reset password code");
        }

        resetPasswordCodeRepository.delete(resetPasswordCode);
    }
}
