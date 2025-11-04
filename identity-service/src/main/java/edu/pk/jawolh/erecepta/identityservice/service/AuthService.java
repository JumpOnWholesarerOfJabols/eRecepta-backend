package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final ResetPasswordCodeService resetPasswordCodeService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(String email, String password, String pesel, Gender gender) {
        if (userRepository.existsByPeselOrEmail(email, pesel)) {
            throw new IllegalArgumentException("User with given PESEL or email already exists");
        }
        // TODO:
        //  -Validate PESEL
        //  -Validate data

        UserAccount account = UserAccount.builder()
                .email(email)
                .pesel(pesel)
                .hashedPassword(passwordEncoder.encode(password))
                .role(UserRole.PATIENT)
                .userGender(GenderMapper.mapGender(gender))
                .verified(false)
                .build();

        UserAccount savedUser = userRepository.save(account);

        // TODO: mail with verification code
        String verificationCode = verificationCodeService.generateVerificationCode(savedUser.getEmail(), savedUser.getPesel());
        log.info("Generated verification code: {}", verificationCode);

        return "User registered successfully";
    }

    public String verifyAccount(String login, String code) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(
                        ()-> new IllegalArgumentException("User with given PESEL or email does not exist"));

        verificationCodeService.verifyVerificationCode(account.getEmail(), account.getPesel(), code);

        account.setVerified(true);
        userRepository.save(account);

        return "Account verified successfully";
    }

    public JwtTokenDTO login(String login, String password) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(
                        ()-> new IllegalArgumentException("User with given PESEL or email does not exist"));

        if (!passwordEncoder.matches(password, account.getHashedPassword()))
            throw new IllegalArgumentException("Wrong password");

        if (!account.isVerified())
            throw new IllegalStateException("Account is not verified");

        return jwtService.generateToken(account.getId());
    }

    public String resetPasswordRequest(String login) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // TODO: mail with reset password code
        String code = resetPasswordCodeService.generateResetPasswordCode(account.getEmail(), account.getPesel());
        log.info("Generated reset password code: {}", code);

        return "Reset password request successfully";
    }

    public String resetPassword(String login, String password, String code) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        resetPasswordCodeService.verifyResetPasswordCode(login, login, code);

        account.setHashedPassword(passwordEncoder.encode(password));
        userRepository.save(account);

        return "Reset password successfully";
    }

    public String sendVerificationCode(String login) {
        UserAccount account = userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //TODO: send mail with verification code
        String code = verificationCodeService.generateVerificationCode(account.getEmail(), account.getPesel());
        log.info("Generated verification code: {}", code);

        return "Verification code sent";
    }
}
