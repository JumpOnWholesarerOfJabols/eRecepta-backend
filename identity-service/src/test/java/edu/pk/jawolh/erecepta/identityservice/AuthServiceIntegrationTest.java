package edu.pk.jawolh.erecepta.identityservice;

import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.identityservice.client.RabbitMQClient;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.exception.*;
import edu.pk.jawolh.erecepta.identityservice.mapper.GenderMapper;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.repository.UserRepository;
import edu.pk.jawolh.erecepta.identityservice.service.AuthService;
import edu.pk.jawolh.erecepta.identityservice.service.JwtService;
import edu.pk.jawolh.erecepta.identityservice.service.ResetPasswordCodeService;
import edu.pk.jawolh.erecepta.identityservice.service.VerificationCodeService;
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
            when(verificationCodeService.generateVerificationCode(data.email, data.pesel)).thenReturn("123456");

            String result = authService.registerUser(data.email, data.pesel, data.firstName, data.lastName,
                    data.phoneNumber, data.gender, data.dateOfBirth, data.password);

            assertEquals("User registered successfully", result);
            Optional<UserAccount> savedUserOpt = userRepository.findByPeselOrEmail(data.pesel, data.email);
            assertTrue(savedUserOpt.isPresent());
            verify(rabbitMQClient).sendVerificationCodeEvent(eq(data.email), anyString());
        }

        @Test
        void shouldThrowUserAlreadyExistsException_whenUserExists() {
            RegistrationData data = RegistrationData.valid().build();
            authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                    data.gender, data.dateOfBirth, data.password);

            assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(data.email, data.pesel,
                    data.firstName, data.lastName, data.phoneNumber, data.gender, data.dateOfBirth, data.password));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselChecksumIsInvalid() {
            RegistrationData data = RegistrationData.valid().pesel("00301086214").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password));
            assertTrue(ex.getErrors().containsKey("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselDoesNotMatchDateOfBirth() {
            RegistrationData data = RegistrationData.valid().dateOfBirth("2000-10-11").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password));
            assertEquals("Date of birth does not match PESEL", ex.getErrors().get("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPeselDoesNotMatchGender() {
            RegistrationData data = RegistrationData.valid().gender(Gender.FEMALE).build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password));
            assertEquals("Gender does not match PESEL", ex.getErrors().get("pesel"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenEmailIsInvalid() {
            RegistrationData data = RegistrationData.valid().email("invalid-email").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password));
            assertEquals("Invalid email address format", ex.getErrors().get("email"));
        }

        @Test
        void shouldThrowMultiFieldValidationException_whenPasswordIsTooShort() {
            RegistrationData data = RegistrationData.valid().password("Short1").build();
            MultiFieldValidationException ex = assertThrows(MultiFieldValidationException.class, () ->
                    authService.registerUser(data.email, data.pesel, data.firstName, data.lastName, data.phoneNumber,
                            data.gender, data.dateOfBirth, data.password));
            assertTrue(ex.getErrors().get("password").contains("at least 8 characters long"));
        }
    }

    @Nested
    class VerifyAccountTests {
        @Test
        void shouldSucceed_whenCodeIsCorrect() {
            UserAccount user = saveUnverifiedUser();
            String correctCode = "valid-code";
            doNothing().when(verificationCodeService).verifyVerificationCode(user.getEmail(), user.getPesel(), correctCode);

            String result = authService.verifyAccount(user.getEmail(), correctCode);

            assertEquals("Account verified successfully", result);
            UserAccount verifiedUser = userRepository.findById(user.getId()).orElseThrow();
            assertTrue(verifiedUser.isVerified());
        }

        @Test
        void shouldThrowAccountVerificationException_whenAccountIsAlreadyVerified() {
            UserAccount user = saveVerifiedUser();
            assertThrows(AccountVerificationException.class, () -> authService.verifyAccount(user.getEmail(), "any-code"));
        }
    }

    @Nested
    class LoginTests {
        @Test
        void shouldSucceed_whenCredentialsAreCorrectAndUserIsVerified() {
            String password = "Password123!";
            UserAccount user = saveVerifiedUser(password);
            when(jwtService.generateToken(user.getId())).thenReturn(new JwtTokenDTO("test-token", ""));

            JwtTokenDTO token = authService.login(user.getEmail(), password);

            assertNotNull(token);
            assertEquals("test-token", token.token());
        }

        @Test
        void shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
            UserAccount user = saveVerifiedUser("correct-password");
            assertThrows(InvalidCredentialsException.class, () -> authService.login(user.getEmail(), "wrong-password"));
        }

        @Test
        void shouldThrowAccountVerificationException_whenAccountIsNotVerified() {
            String password = "Password123!";
            UserAccount user = saveUnverifiedUser(password);
            assertThrows(AccountVerificationException.class, () -> authService.login(user.getEmail(), password));
        }

        @Test
        void shouldThrowUserDoesNotExistException_whenUserIsNotFound() {
            assertThrows(UserDoesNotExistException.class, () -> authService.login("nonexistent@user.com", "any-password"));
        }
    }

    @Nested
    class ResetPasswordRequestTests {
        @Test
        void shouldSucceed() {
            UserAccount user = saveVerifiedUser();
            String code = "reset-code-123";
            when(resetPasswordCodeService.generateResetPasswordCode(user.getEmail(), user.getPesel())).thenReturn(code);

            String result = authService.resetPasswordRequest(user.getEmail());

            assertEquals("Reset password request successfully", result);
            verify(rabbitMQClient).sendResetPasswordCodeEvent(user.getEmail(), code);
        }
    }

    @Nested
    class ResetPasswordTests {
        @Test
        void shouldSucceed_whenCodeIsValid() {
            UserAccount user = saveVerifiedUser("oldPassword1!");
            String newPassword = "newValidPassword1!";
            String code = "valid-reset-code";
            doNothing().when(resetPasswordCodeService).verifyResetPasswordCode(user.getEmail(), user.getEmail(), code);

            String result = authService.resetPassword(user.getEmail(), newPassword, code);

            assertEquals("Reset password successfully", result);
            UserAccount updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertTrue(passwordEncoder.matches(newPassword, updatedUser.getHashedPassword()));
        }

        @Test
        void shouldThrowException_whenCodeVerificationFails() {
            UserAccount user = saveVerifiedUser();
            String invalidCode = "invalid-code";
            doThrow(new InvalidCredentialsException("Code has expired"))
                    .when(resetPasswordCodeService).verifyResetPasswordCode(user.getEmail(), user.getEmail(), invalidCode);

            assertThrows(InvalidCredentialsException.class, () ->
                    authService.resetPassword(user.getEmail(), "new-password", invalidCode));
        }
    }

    @Nested
    class SendVerificationCodeTests {
        @Test
        void shouldSucceed_whenUserIsUnverified() {
            UserAccount user = saveUnverifiedUser();
            String newCode = "new-code-456";
            when(verificationCodeService.generateVerificationCode(user.getEmail(), user.getPesel())).thenReturn(newCode);

            String result = authService.sendVerificationCode(user.getEmail());

            assertEquals("Verification code sent", result);
            verify(rabbitMQClient).sendVerificationCodeEvent(user.getEmail(), newCode);
        }

        @Test
        void shouldThrowAccountVerificationException_whenUserIsVerified() {
            UserAccount user = saveVerifiedUser();
            assertThrows(AccountVerificationException.class, () -> authService.sendVerificationCode(user.getEmail()));
        }
    }
}