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
import edu.pk.jawolh.erecepta.identityservice.mapper.AuditLogMapper;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.mapper.LoginAttemptMapper;
import edu.pk.jawolh.erecepta.identityservice.mapper.UserMapper;
import edu.pk.jawolh.erecepta.identityservice.model.AuditLog;
import edu.pk.jawolh.erecepta.identityservice.model.LoginAttempt;
import edu.pk.jawolh.erecepta.identityservice.model.RefreshToken;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.repository.AuditLogRepository;
import edu.pk.jawolh.erecepta.identityservice.repository.LoginAttemptRepository;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.validation.RegisterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private final AuditLogRepository auditLogRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    public String registerUser(
            String email,
            String pesel,
            String firstName,
            String lastName,
            String phoneNumber,
            Gender gender,
            String dateOfBirth,
            String password,
            String ipAddress
    ) {
        return registerUser(email, pesel, firstName, lastName, phoneNumber, gender, dateOfBirth, password, UserRole.PATIENT, ipAddress);
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
            UserRole role,
            String ipAddress
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

        logAction(savedUser.getId(), "REGISTER_USER", ipAddress);

        return "User registered successfully";
    }

    @Transactional(noRollbackFor = {
            InvalidCredentialsException.class,
            AccountVerificationException.class
    })
    public AuthToken login(String login, String password, String ipAddress) {
        UserAccount account = getAccount(login);

        if (!passwordEncoder.matches(password, account.getHashedPassword())) {
            saveLoginAttempt(account.getId(), ipAddress, false);
            logAction(account.getId(), "LOGIN_FAILED", ipAddress);
            throw new InvalidCredentialsException("Wrong password");
        }

        if (!account.isVerified()) {
            logAction(account.getId(), "LOGIN_BLOCKED_NOT_VERIFIED", ipAddress);
            throw new AccountVerificationException("Account is not verified");
        }

        saveLoginAttempt(account.getId(), ipAddress, true);
        logAction(account.getId(), "LOGIN_SUCCESS", ipAddress);

        JwtTokenDTO accessToken = jwtService.generateToken(account.getId(), account.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(account.getId());

        return AuthToken.newBuilder()
                .token(accessToken.token())
                .expiresAt(accessToken.expiresAt())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthToken refreshToken(String requestRefreshToken, String ipAddress) {
        RefreshToken token = refreshTokenService.findByToken(requestRefreshToken);
        refreshTokenService.verifyExpiration(token);

        UserAccount user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        refreshTokenService.deleteByToken(requestRefreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        JwtTokenDTO newAccessToken = jwtService.generateToken(user.getId(), user.getRole());

        logAction(user.getId(), "REFRESH_TOKEN", ipAddress);

        return AuthToken.newBuilder()
                .token(newAccessToken.token())
                .expiresAt(newAccessToken.expiresAt())
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public String logout(String refreshToken, String ipAddress) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.deleteByToken(refreshToken);

        logAction(token.getUserId(), "LOGOUT", ipAddress);

        return "Logged out successfully";
    }

    public String logoutFromOtherDevices(String refreshToken, String ipAddress) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        refreshTokenService.deleteByUserIdAndTokenNot(token.getUserId(), refreshToken);

        logAction(token.getUserId(), "LOGOUT_OTHER_DEVICES", ipAddress);

        return "Logged out from other devices successfully";
    }

    public String verifyAccount(String login, String code, String ipAddress) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        log.info("Verification data check: email={}, pesel={}, code={}",
                account.getEmail(), account.getPesel(), code);

        verificationCodeService.verifyVerificationCode(account.getId(), code);

        account.setVerified(true);
        userRepository.save(account);

        logAction(account.getId(), "VERIFY_ACCOUNT", ipAddress);

        return "Account verified successfully";
    }


    public String resetPasswordRequest(String login, String ipAddress) {
        UserAccount account = getAccount(login);

        String code = resetPasswordCodeService.generateResetPasswordCode(account.getId());
        rabbitMQClient.sendResetPasswordCodeEvent(account.getEmail(), code);
        log.info("Generated reset password code: {}", code);

        logAction(account.getId(), "RESET_PASSWORD_REQUEST", ipAddress);

        return "Reset password request successfully";
    }

    public String resetPassword(String login, String password, String code, String ipAddress) {
        UserAccount account = getAccount(login);

        if (!account.isVerified())
            throw new AccountVerificationException("Account is not verified");

        resetPasswordCodeService.verifyResetPasswordCode(account.getId(), code);

        account.setHashedPassword(passwordEncoder.encode(password));
        userRepository.save(account);

        refreshTokenService.deleteAllByUserId(account.getId());

        logAction(account.getId(), "RESET_PASSWORD_SUCCESS", ipAddress);

        return "Reset password successfully";
    }

    public String sendVerificationCode(String login, String ipAddress) {
        UserAccount account = getAccount(login);

        if (account.isVerified())
            throw new AccountVerificationException("Account is already verified");

        String code = verificationCodeService.generateVerificationCode(account.getId());
        rabbitMQClient.sendVerificationCodeEvent(account.getEmail(), code);
        log.info("Generated verification code: {}", code);

        logAction(account.getId(), "SEND_VERIFICATION_CODE", ipAddress);

        return "Verification code sent";
    }

    private UserAccount getAccount(String login) {
        return userRepository.findByPeselOrEmail(login, login)
                .orElseThrow(
                        () -> new UserDoesNotExistException("User with given PESEL or email does not exist"));
    }

    private void logAction(UUID userId, String action, String ipAddress) {
        try {
            auditLogRepository.save(AuditLog.builder()
                    .userId(userId)
                    .actionName(action)
                    .ipAddress(ipAddress)
                    .logDate(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Failed to save audit log for user {}", userId, e);
        }
    }

    private void saveLoginAttempt(UUID userId, String ipAddress, boolean success) {
        try {
            loginAttemptRepository.save(LoginAttempt.builder()
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .success(success)
                    .attemptDate(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Failed to save login attempt for user {}", userId, e);
        }
    }

    @Transactional(readOnly = true)
    public List<com.example.demo.codegen.types.LoginAttempt> getUserLoginAttempts(UUID userId) {
        return loginAttemptRepository.findAllByUserId(userId).stream()
                .map(LoginAttemptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.example.demo.codegen.types.AuditLog> getUserAuditLogs(UUID userId) {
        return auditLogRepository.findAllByUserId(userId).stream()
                .map(AuditLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UUID getUserIdByPeselOrEmail(String identifier) {
        return userRepository.findIdByPeselOrEmail(identifier)
                .orElseThrow(() -> new UserDoesNotExistException("User with given PESEL or email not found"));
    }

    @Transactional(readOnly = true)
    public com.example.demo.codegen.types.UserAccount getUserInfoById(UUID id) {
        UserAccount userAccount = userRepository.findById(id).orElseThrow(
                () -> new UserDoesNotExistException("User with given id does not exists")
        );

        return UserMapper.toDTO(userAccount);
    }
}
