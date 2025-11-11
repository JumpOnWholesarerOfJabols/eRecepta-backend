package edu.pk.jawolh.erecepta.identityservice.validation;

import edu.pk.jawolh.erecepta.identityservice.exception.MultiFieldValidationException;
import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RegisterValidatorTest {

    @Mock
    private PeselValidator peselValidator;

    @InjectMocks
    private RegisterValidator registerValidator;

    private static final String VALID_PESEL = "00301086213";
    private static final LocalDate VALID_DOB = LocalDate.of(2000, 10, 10);
    private static final UserGender VALID_GENDER = UserGender.MALE;
    private static final String VALID_EMAIL = "valid.user@example.com";
    private static final String VALID_FIRST_NAME = "Jan";
    private static final String VALID_LAST_NAME = "Kowalski";
    private static final String VALID_PHONE = "123456789";
    private static final String VALID_PASSWORD = "StrongPassword123!";

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> verifyNoInteractions(peselValidator));
    }

    private void runValidation(String pesel, LocalDate dob, UserGender gender, String email, String firstName, String lastName, String phoneNumber, String password) {
        registerValidator.validateRegistrationData(pesel, dob, gender, email, firstName, lastName, phoneNumber, password);
    }

    @Test
    void validateRegistrationData_shouldSucceedForValidData() {
        assertDoesNotThrow(() -> runValidation(
                VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD
        ));
        verify(peselValidator).validate(VALID_PESEL, VALID_DOB, VALID_GENDER);
    }

    @Nested
    class EmailValidationTests {
        @Test
        void validateRegistrationData_shouldFailForEmptyEmail() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, "", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("email", "Invalid email address format");
        }

        @Test
        void validateRegistrationData_shouldFailForInvalidEmailFormat() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, "invalid-email@", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("email", "Invalid email address format");
        }

        @Test
        void validateRegistrationData_shouldFailForNullEmail() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, null, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("email", "Invalid email address format");
        }
    }

    @Nested
    class FirstNameValidationTests {
        @Test
        void validateRegistrationData_shouldFailForEmptyFirstName() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, "  ", VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("firstName", "Invalid first name");
        }

        @Test
        void validateRegistrationData_shouldFailForFirstNameStartingWithLowercase() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, "jan", VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("firstName", "Invalid first name");
        }

        @Test
        void validateRegistrationData_shouldSucceedForValidFirstNameWithPolishChars() {
            assertDoesNotThrow(() ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, "Łukasz", VALID_LAST_NAME, VALID_PHONE, VALID_PASSWORD)
            );
        }
    }

    @Nested
    class LastNameValidationTests {
        @Test
        void validateRegistrationData_shouldFailForEmptyLastName() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, "", VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("lastName", "Invalid last name");
        }

        @Test
        void validateRegistrationData_shouldFailForLastNameStartingWithLowercase() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, "kowalski", VALID_PHONE, VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("lastName", "Invalid last name");
        }

        @Test
        void validateRegistrationData_shouldSucceedForValidLastNameWithApostropheAndDash() {
            assertDoesNotThrow(() ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, "Smith-O'Neil", VALID_PHONE, VALID_PASSWORD)
            );
        }
    }

    @Nested
    class PhoneNumberValidationTests {
        @Test
        void validateRegistrationData_shouldSucceedForEmptyPhoneNumber() {
            assertDoesNotThrow(() ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PASSWORD)
            );
        }

        @Test
        void validateRegistrationData_shouldSucceedForValidPhoneNumberWithPlus() {
            assertDoesNotThrow(() ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, "+48500600700", VALID_PASSWORD)
            );
        }

        @Test
        void validateRegistrationData_shouldFailForInvalidPhoneNumberFormat() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, "abc", VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("phoneNumber", "Invalid phone number format");
        }

        @Test
        void validateRegistrationData_shouldFailForTooShortPhoneNumber() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, "12345678", VALID_PASSWORD)
            );
            assertThat(exception.getErrors()).containsEntry("phoneNumber", "Invalid phone number format");
        }
    }

    @Nested
    class PasswordValidationTests {
        @Test
        void validatePassword_shouldFailForEmptyPassword() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, "")
            );
            assertThat(exception.getErrors()).containsEntry("password", "Password cannot be empty");
            assertThat(exception.getErrors().size()).isEqualTo(1);
        }

        @Test
        void validatePassword_shouldFailForTooShortPassword() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, "P1a!6")
            );
            Map<String, String> errors = exception.getErrors();
            assertThat(errors).containsEntry("password", "Password must be at least 8 characters long");
            assertThat(errors.size()).isEqualTo(1);
        }

        @Test
        void validatePassword_shouldFailIfNoUppercase() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, "pstrongpassword123!")
            );
            assertThat(exception.getErrors()).containsEntry("password", "Password must contain at least one uppercase letter");
        }

        @Test
        void validatePassword_shouldFailIfNoLowercase() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, "PSTRONGPASSWORD123!")
            );
            assertThat(exception.getErrors()).containsEntry("password", "Password must contain at least one lowercase letter");
        }

        @Test
        void validatePassword_shouldFailIfNoDigit() {
            MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                    runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, VALID_EMAIL, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_PHONE, "StrongPassword!")
            );
            assertThat(exception.getErrors()).containsEntry("password", "Password must contain at least one number");
        }
    }

    @Test
    void validateRegistrationData_shouldCatchMultipleErrors() {
        // Given
        String errorMessage = "Pesel error";
        doThrow(new ValidationException(errorMessage))
                .when(peselValidator).validate(anyString(), any(LocalDate.class), any(UserGender.class));

        // When
        MultiFieldValidationException exception = assertThrows(MultiFieldValidationException.class, () ->
                runValidation(VALID_PESEL, VALID_DOB, VALID_GENDER, "invalid-email", "", "kowalski", VALID_PHONE, "pass")
        );

        // Then
        Map<String, String> errors = exception.getErrors();
        assertThat(errors).hasSize(5); // pesel, email, firstName, lastName, password
        assertThat(errors)
                .containsEntry("pesel", errorMessage)
                .containsEntry("email", "Invalid email address format")
                .containsEntry("firstName", "Invalid first name")
                .containsEntry("lastName", "Invalid last name")
                .containsKey("password");
    }
}
