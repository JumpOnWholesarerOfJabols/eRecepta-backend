package edu.pk.jawolh.erecepta.identityservice;

import com.example.demo.codegen.types.AuthToken;
import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.identityservice.client.RabbitMQClient;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.exception.*;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.model.RefreshToken;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.repository.AuditLogRepository;
import edu.pk.jawolh.erecepta.identityservice.repository.LoginAttemptRepository;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.service.*;
import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RabbitMQClient rabbitMQClient;
    @MockitoBean
    private VerificationCodeService verificationCodeService;
    @MockitoBean
    private ResetPasswordCodeService resetPasswordCodeService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private AuditLogRepository auditLogRepository;
    @MockitoBean
    private LoginAttemptRepository loginAttemptRepository;

    private final String clientIp = "127.0.0.1";

    @Builder(toBuilder = true)
    private record RegistrationData(
            String email, String pesel, String firstName, String lastName,
            String phoneNumber, Gender gender, String dateOfBirth, String password
    ) {
        public static RegistrationDataBuilder valid() {
            return new RegistrationDataBuilder()
                    .email("jan.kowalski@example.com")
                    .pesel("00301086213")
                    .firstName("Jan")
                    .lastName("Kowalski")
                    .phoneNumber("123456789")
                    .gender(Gender.MALE)
                    .dateOfBirth("2000-10-10")
                    .password("ValidPassword123!");
        }
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private UserAccount saveUnverifiedUser(String password) {
        RegistrationData data = RegistrationData.valid().build();
        UserAccount account = UserAccount.builder()
                .email(data.email)
                .pesel(data.pesel)
                .firstName(data.firstName)
                .lastName(data.lastName)
                .dateOfBirth(LocalDate.parse(data.dateOfBirth))
                .role(UserRole.PATIENT)
                .userGender(GenderMapper.mapGender(data.gender))
                .phoneNumber(data.phoneNumber)
                .hashedPassword(passwordEncoder.encode(password))
                .verified(false).build();
        return userRepository.save(account);
    }
    private UserAccount saveUnverifiedUser() { return saveUnverifiedUser("defaultPass1!"); }

    private UserAccount saveVerifiedUser(String password) {
        UserAccount account = saveUnverifiedUser(password);
        account.setVerified(true);
        return userRepository.save(account);
    }

    private UserAccount saveVerifiedUser() { return saveVerifiedUser("defaultPass1!"); }


    @Nested
    class RegisterUserTests {

        @Test
        void shouldSucceed_whenDataIsValid() {
            RegistrationData data = RegistrationData.valid().build();
            when(verificationCodeService.generateVerificationCode(any(UUID.class))).thenReturn("123456");

            String result = authService.registerUser(data.email, data.pesel, data.firstName, data.lastName,
                    data.phoneNumber, data.gender, data.dateOfBirth, data.password, clientIp);

            assertEquals("User registered successfully", result);
            Optional<UserAccount> savedUserOpt = userRepository.findByPeselOrEmail(data.pesel, data.email);
            assertTrue(savedUserOpt.isPresent());

            verify(rabbitMQClient).sendVerificationCodeEvent(eq(data.email), anyString());
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowUserAlreadyExistsException_whenUserExists() {
            RegistrationData data = RegistrationData.valid().build();
            authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                    data.gender, data.dateOfBirth, data.password, clientIp);

            assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(data.email, data.pesel,
                    data.firstName, data.lastName, data.phoneNumber, data.gender, data.dateOfBirth, data.password,clientIp));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselChecksumIsInvalid() {
            RegistrationData data = RegistrationData.valid().pesel("00301086214").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password,clientIp));
            assertTrue(ex.getErrors().containsKey("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselDoesNotMatchDateOfBirth() {
            RegistrationData data = RegistrationData.valid().dateOfBirth("2000-10-11").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password,clientIp));
            assertEquals("Date of birth does not match PESEL", ex.getErrors().get("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselDoesNotMatchGender() {
            RegistrationData data = RegistrationData.valid().gender(Gender.FEMALE).build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password,clientIp));
            assertEquals("Gender does not match PESEL", ex.getErrors().get("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenEmailIsInvalid() {
            RegistrationData data = RegistrationData.valid().email("invalid-email").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password,clientIp));
            assertEquals("Invalid email address format", ex.getErrors().get("email"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPasswordIsTooShort() {
            RegistrationData data = RegistrationData.valid().password("Short1").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password,clientIp));
            assertTrue(ex.getErrors().get("password").contains("at least 8 characters long"));
        }
    }

    @Nested
    class VerifyAccountTests {
        @Test
        void shouldSucceed_whenCodeIsCorrect() {
            UserAccount user = saveUnverifiedUser();
            String correctCode = "valid-code";
            doNothing().when(verificationCodeService).verifyVerificationCode(user.getId(), correctCode);

            String result = authService.verifyAccount(user.getEmail(), correctCode,clientIp);

            assertEquals("Account verified successfully", result);
            UserAccount verifiedUser = userRepository.findById(user.getId()).orElseThrow();
            assertTrue(verifiedUser.isVerified());
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowAccountVerificationException_whenAccountIsAlreadyVerified() {
            UserAccount user = saveVerifiedUser();
            assertThrows(AccountVerificationException.class, () -> authService.verifyAccount(user.getEmail(), "any-code",clientIp));
        }
    }

    @Nested
    class LoginTests {
        @Test
        void shouldSucceed_whenCredentialsAreCorrectAndUserIsVerified() {
            String password = "Password123!";
            UserAccount user = saveVerifiedUser(password);
            when(jwtService.generateToken(user.getId(), user.getRole()))
                    .thenReturn(new JwtTokenDTO("access-token-jwt", "2024-12-31"));

            RefreshToken mockRefreshToken = createMockRefreshToken(user.getId(), "refresh-token-uuid");
            when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(mockRefreshToken);

            AuthToken result = authService.login(user.getEmail(), password, clientIp);

            assertNotNull(result);
            assertEquals("access-token-jwt", result.getToken());
            assertEquals("refresh-token-uuid", result.getRefreshToken());

            verify(loginAttemptRepository).save(any());
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
            UserAccount user = saveVerifiedUser("correct-password");
            assertThrows(InvalidCredentialsException.class, () -> authService.login(user.getEmail(), "wrong-password", clientIp));

            verify(loginAttemptRepository).save(any());
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowAccountVerificationException_whenAccountIsNotVerified() {
            String password = "Password123!";
            UserAccount user = saveUnverifiedUser(password);
            assertThrows(AccountVerificationException.class, () -> authService.login(user.getEmail(), password, clientIp));
        }

        @Test
        void shouldThrowUserDoesNotExistException_whenUserIsNotFound() {
            assertThrows(UserDoesNotExistException.class, () -> authService.login("nonexistent@user.com", "any-password", clientIp));
        }
    }

    @Nested
    class ResetPasswordRequestTests {
        @Test
        void shouldSucceed() {
            UserAccount user = saveVerifiedUser();
            String code = "reset-code-123";
            when(resetPasswordCodeService.generateResetPasswordCode(user.getId())).thenReturn(code);

            String result = authService.resetPasswordRequest(user.getEmail(),clientIp);

            assertEquals("Reset password request successfully", result);
            verify(rabbitMQClient).sendResetPasswordCodeEvent(user.getEmail(), code);
            verify(auditLogRepository).save(any());
        }
    }

    @Nested
    class ResetPasswordTests {
        @Test
        void shouldSucceed_whenCodeIsValid() {
            UserAccount user = saveVerifiedUser("oldPassword1!");
            String newPassword = "newValidPassword1!";
            String code = "valid-reset-code";

            doNothing().when(resetPasswordCodeService).verifyResetPasswordCode(user.getId(), code);
            doNothing().when(refreshTokenService).deleteAllByUserId(user.getId());

            String result = authService.resetPassword(user.getEmail(), newPassword, code,clientIp);

            UserAccount updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertTrue(passwordEncoder.matches(newPassword, updatedUser.getHashedPassword()));

            verify(refreshTokenService).deleteAllByUserId(user.getId());
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowException_whenCodeVerificationFails() {
            UserAccount user = saveVerifiedUser();
            String invalidCode = "invalid-code";
            doThrow(new InvalidCredentialsException("Code has expired"))
                    .when(resetPasswordCodeService).verifyResetPasswordCode(user.getId(), invalidCode);

            assertThrows(InvalidCredentialsException.class, () ->
                    authService.resetPassword(user.getEmail(), "new-password", invalidCode,clientIp));
        }
    }

    @Nested
    class SendVerificationCodeTests {
        @Test
        void shouldSucceed_whenUserIsUnverified() {
            UserAccount user = saveUnverifiedUser();
            String newCode = "new-code-456";
            when(verificationCodeService.generateVerificationCode(user.getId())).thenReturn(newCode);

            String result = authService.sendVerificationCode(user.getEmail(),clientIp);

            assertEquals("Verification code sent", result);
            verify(rabbitMQClient).sendVerificationCodeEvent(user.getEmail(), newCode);
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowAccountVerificationException_whenUserIsVerified() {
            UserAccount user = saveVerifiedUser();
            assertThrows(AccountVerificationException.class, () -> authService.sendVerificationCode(user.getEmail(),clientIp));
        }
    }



    private RefreshToken createMockRefreshToken(UUID userId, String tokenString) {
        return RefreshToken.builder()
                .userId(userId)
                .token(tokenString)
                .expiryDate(java.time.LocalDateTime.now().plusHours(1))
                .build();
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void shouldSucceed_whenRefreshTokenIsValid() {
            UserAccount user = saveVerifiedUser();
            String oldRefreshTokenStr = "old-valid-token";
            RefreshToken oldToken = createMockRefreshToken(user.getId(), oldRefreshTokenStr);
            String newRefreshTokenStr = "new-valid-token";
            RefreshToken newToken = createMockRefreshToken(user.getId(), newRefreshTokenStr);

            when(refreshTokenService.findByToken(oldRefreshTokenStr)).thenReturn(oldToken);
            doNothing().when(refreshTokenService).verifyExpiration(oldToken);
            doNothing().when(refreshTokenService).deleteByToken(oldRefreshTokenStr);
            when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(newToken);
            when(jwtService.generateToken(user.getId(), user.getRole()))
                    .thenReturn(new JwtTokenDTO("new-access-jwt", "tomorrow"));

            AuthToken result = authService.refreshToken(oldRefreshTokenStr,clientIp);

            assertNotNull(result);
            assertEquals("new-access-jwt", result.getToken());
            assertEquals(newRefreshTokenStr, result.getRefreshToken());

            verify(refreshTokenService).deleteByToken(oldRefreshTokenStr);
            verify(auditLogRepository).save(any());
        }

        @Test
        void shouldThrowUserDoesNotExist_whenUserFromTokenNotFound() {
            String tokenStr = "orphan-token";
            UUID nonExistentUserId = UUID.randomUUID();
            RefreshToken token = createMockRefreshToken(nonExistentUserId, tokenStr);

            when(refreshTokenService.findByToken(tokenStr)).thenReturn(token);

            assertThrows(UserDoesNotExistException.class, () -> authService.refreshToken(tokenStr,clientIp));
        }
    }

    @Nested
    class LogoutTests {

        @Test
        void logout_shouldDeleteToken() {
            String tokenStr = "some-refresh-token";
            UUID userId = UUID.randomUUID();
            RefreshToken mockToken = createMockRefreshToken(userId, tokenStr);

            when(refreshTokenService.findByToken(tokenStr)).thenReturn(mockToken);
            doNothing().when(refreshTokenService).deleteByToken(tokenStr);

            String result = authService.logout(tokenStr, clientIp);

            assertEquals("Logged out successfully", result);
            verify(refreshTokenService).deleteByToken(tokenStr);
            verify(auditLogRepository).save(any());
        }

        @Test
        void logoutFromOtherDevices_shouldDeleteOtherTokens() {
            UserAccount user = saveVerifiedUser();
            String currentTokenStr = "current-token";
            RefreshToken currentToken = createMockRefreshToken(user.getId(), currentTokenStr);

            when(refreshTokenService.findByToken(currentTokenStr)).thenReturn(currentToken);
            doNothing().when(refreshTokenService).deleteByUserIdAndTokenNot(user.getId(), currentTokenStr);

            String result = authService.logoutFromOtherDevices(currentTokenStr, clientIp);

            assertEquals("Logged out from other devices successfully", result);
            verify(refreshTokenService).deleteByUserIdAndTokenNot(user.getId(), currentTokenStr);
            verify(auditLogRepository).save(any());
        }
    }
}