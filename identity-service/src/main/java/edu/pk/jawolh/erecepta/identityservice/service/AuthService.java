package edu.pk.jawolh.erecepta.identityservice.service;

import com.example.demo.codegen.types.AuthToken;
import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.common.user.enums.UserGender;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.identityservice.client.RabbitMQClient;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.exception.AccountVerificationException;
import edu.pk.jawolh.erecepta.identityservice.exception.InvalidCredentialsException;
import edu.pk.jawolh.erecepta.identityservice.exception.UserAlreadyExistsException;
import edu.pk.jawolh.erecepta.identityservice.exception.UserDoesNotExistException;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.model.RefreshToken;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.validation.RegisterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final ResetPasswordCodeService resetPasswordCodeService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RegisterValidator registerValidator;
    private final RabbitMQClient rabbitMQClient;
    private final RefreshTokenService refreshTokenService;

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
        return registerUser(email, pesel, firstName, lastName, phoneNumber, gender, dateOfBirth, password, UserRole.PATIENT);
    }

    public String registerUser(
            String email,
            String pesel,
            String firstName,
            String lastName,
            String phoneNumber,
            Gender gender,
            String dateOfBirth,
            String password,
            UserRole role
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
                .role(role)
                .verified(false)
                .build();

        log.info("Registering user: {}", account);

        UserAccount savedUser = userRepository.save(account);

        String verificationCode = verificationCodeService.generateVerificationCode(account.getId());
        rabbitMQClient.sendVerificationCodeEvent(savedUser.getEmail(), verificationCode);

        return "User registered successfully";
    }

    public AuthToken refreshToken(String requestRefreshToken) {
        RefreshToken token = refreshTokenService.findByToken(requestRefreshToken);
        refreshTokenService.verifyExpiration(token);

        UserAccount user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        refreshTokenService.deleteByToken(requestRefreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        JwtTokenDTO newAccessToken = jwtService.generateToken(user.getId(), user.getRole());

        return AuthToken.newBuilder()
                .token(newAccessToken.token())
                .expiresAt(newAccessToken.expiresAt())
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public String logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
        return "Logged out successfully";
    }

    public String logoutFromOtherDevices(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.deleteByUserIdAndTokenNot(token.getUserId(), refreshToken);
        return "Logged out from other devices successfully";
    }

    public String verifyAccount(String login, String code) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        log.info("Verification data check: email={}, pesel={}, code={}",
                account.getEmail(), account.getPesel(), code);

        verificationCodeService.verifyVerificationCode(account.getId(), code);

        account.setVerified(true);
        userRepository.save(account);

        return "Account verified successfully";
    }

    public AuthToken login(String login, String password) {
        UserAccount account = getAccount(login);

        if (!passwordEncoder.matches(password, account.getHashedPassword()))
            throw new InvalidCredentialsException("Wrong password");

        if (!account.isVerified())
            throw new AccountVerificationException("Account is not verified");

        JwtTokenDTO accessToken = jwtService.generateToken(account.getId(), account.getRole());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(account.getId());

        return AuthToken.newBuilder()
                .token(accessToken.token())
                .expiresAt(accessToken.expiresAt())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public String resetPasswordRequest(String login) {
        UserAccount account = getAccount(login);

        String code = resetPasswordCodeService.generateResetPasswordCode(account.getId());
        rabbitMQClient.sendResetPasswordCodeEvent(account.getEmail(), code);
        log.info("Generated reset password code: {}", code);

        return "Reset password request successfully";
    }

    public String resetPassword(String login, String password, String code) {
        UserAccount account = getAccount(login);

        if (!account.isVerified())
            throw new AccountVerificationException("Account is not verified");

        resetPasswordCodeService.verifyResetPasswordCode(account.getId(), code);

        account.setHashedPassword(passwordEncoder.encode(password));
        userRepository.save(account);

        refreshTokenService.deleteAllByUserId(account.getId());

        return "Reset password successfully";
    }

    public String sendVerificationCode(String login) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        String code = verificationCodeService.generateVerificationCode(account.getId());
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
