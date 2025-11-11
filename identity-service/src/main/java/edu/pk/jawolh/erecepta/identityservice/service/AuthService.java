package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.identityservice.client.RabbitMQClient;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.exception.AccountVerificationException;
import edu.pk.jawolh.erecepta.identityservice.exception.InvalidCredentialsException;
import edu.pk.jawolh.erecepta.identityservice.exception.UserAlreadyExistsException;
import edu.pk.jawolh.erecepta.identityservice.exception.UserDoesNotExistException;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.validation.RegisterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final ResetPasswordCodeService resetPasswordCodeService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RegisterValidator registerValidator;
    private final RabbitMQClient rabbitMQClient;

    public String registerUser(
            String email,
            String pesel,
            String firstName,
            String lastName,
            String phoneNumber,
            Gender gender,
            String dateOfBirth,
            String password
    ) {
        if (userRepository.existsByPeselOrEmail(pesel, email)) {
            throw new UserAlreadyExistsException("User with given PESEL or email already exists");
        }

        UserGender userGender = GenderMapper.mapGender(gender);
        LocalDate dateOfBirthParsed = LocalDate.parse(dateOfBirth);

        registerValidator.validateRegistrationData(
                pesel,
                dateOfBirthParsed,
                userGender,
                email,
                firstName,
                lastName,
                phoneNumber,
                password
        );

        UserAccount account = UserAccount.builder()
                .email(email)
                .pesel(pesel)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .userGender(userGender)
                .dateOfBirth(dateOfBirthParsed)
                .hashedPassword(passwordEncoder.encode(password))
                .role(UserRole.PATIENT)
                .verified(false)
                .build();

        log.info("Registering user: {}", account);

        UserAccount savedUser = userRepository.save(account);

        String verificationCode = verificationCodeService.generateVerificationCode(savedUser.getEmail(), savedUser.getPesel());
        rabbitMQClient.sendVerificationCodeEvent(savedUser.getEmail(), verificationCode);

        return "User registered successfully";
    }

    public String verifyAccount(String login, String code) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        log.info("Verification data check: email={}, pesel={}, code={}",
                account.getEmail(), account.getPesel(), code);

        verificationCodeService.verifyVerificationCode(account.getEmail(), account.getPesel(), code);

        account.setVerified(true);
        userRepository.save(account);

        return "Account verified successfully";
    }

    public JwtTokenDTO login(String login, String password) {
        UserAccount account = getAccount(login);

        if (!passwordEncoder.matches(password, account.getHashedPassword()))
            throw new InvalidCredentialsException("Wrong password");

        if (!account.isVerified())
            throw new AccountVerificationException("Account is not verified");

        return jwtService.generateToken(account.getId(), account.getRole());
    }

    public String resetPasswordRequest(String login) {
        UserAccount account = getAccount(login);

        String code = resetPasswordCodeService.generateResetPasswordCode(account.getEmail(), account.getPesel());
        rabbitMQClient.sendResetPasswordCodeEvent(account.getEmail(), code);
        log.info("Generated reset password code: {}", code);

        return "Reset password request successfully";
    }

    public String resetPassword(String login, String password, String code) {
        UserAccount account = getAccount(login);

        if (!account.isVerified())
            throw new AccountVerificationException("Account is not verified");

        resetPasswordCodeService.verifyResetPasswordCode(login, login, code);

        account.setHashedPassword(passwordEncoder.encode(password));
        userRepository.save(account);

        return "Reset password successfully";
    }

    public String sendVerificationCode(String login) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        String code = verificationCodeService.generateVerificationCode(account.getEmail(), account.getPesel());
        rabbitMQClient.sendVerificationCodeEvent(account.getEmail(), code);
        log.info("Generated verification code: {}", code);

        return "Verification code sent";
    }

    private UserAccount getAccount(String login) {
        return userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(
                        () -> new UserDoesNotExistException("User with given PESEL or email does not exist"));
    }
}
