package edu.pk.jawolh.erecepta.identityservice.validation;

import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeselValidatorTest {

    private PeselValidator peselValidator;

    private static final String VALID_PESEL_MALE = "00301085816";
    private static final LocalDate VALID_DOB_MALE = LocalDate.of(2000, 10, 10);
    private static final UserGender VALID_GENDER_MALE = UserGender.MALE;

    private static final String VALID_PESEL_FEMALE = "95051517944";
    private static final LocalDate VALID_DOB_FEMALE = LocalDate.of(1995, 5, 15);
    private static final UserGender VALID_GENDER_FEMALE = UserGender.FEMALE;

    @BeforeEach
    void setUp() {
        peselValidator = new PeselValidator();
    }

    @Test
    void validate_shouldSucceedForValidMaleData() {
        assertDoesNotThrow(() ->
                peselValidator.validate(VALID_PESEL_MALE, VALID_DOB_MALE, VALID_GENDER_MALE)
        );
    }

    @Test
    void validate_shouldSucceedForValidFemaleData() {
        assertDoesNotThrow(() ->
                peselValidator.validate(VALID_PESEL_FEMALE, VALID_DOB_FEMALE, VALID_GENDER_FEMALE)
        );
    }

    @Test
    void validate_shouldSucceedWhenGenderIsNull() {
        assertDoesNotThrow(() ->
                peselValidator.validate(VALID_PESEL_MALE, VALID_DOB_MALE, null)
        );
    }

    @Nested
    class FormatValidationTests {

        @Test
        void validate_shouldThrowExceptionForNullPesel() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate(null, VALID_DOB_MALE, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("PESEL must contain exactly 11 digits");
        }

        @Test
        void validate_shouldThrowExceptionForTooShortPesel() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate("12345", VALID_DOB_MALE, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("PESEL must contain exactly 11 digits");
        }

        @Test
        void validate_shouldThrowExceptionForTooLongPesel() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate("123456789012", VALID_DOB_MALE, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("PESEL must contain exactly 11 digits");
        }

        @Test
        void validate_shouldThrowExceptionForPeselWithLetters() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate("1234567890a", VALID_DOB_MALE, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("PESEL must contain exactly 11 digits");
        }
    }

    @Nested
    class ChecksumValidationTests {

        @Test
        void validate_shouldThrowExceptionForInvalidChecksum() {
            String peselWithInvalidChecksum = "00301086214";
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate(peselWithInvalidChecksum, VALID_DOB_MALE, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("Invalid PESEL checksum");
        }
    }

    @Nested
    class DateValidationTests {

        @Test
        void validate_shouldThrowExceptionWhenDateOfBirthDoesNotMatchPesel() {
            LocalDate wrongDob = VALID_DOB_MALE.plusDays(1);
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate(VALID_PESEL_MALE, wrongDob, VALID_GENDER_MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("Date of birth does not match PESEL");
        }
    }

    @Nested
    class GenderValidationTests {

        @Test
        void validate_shouldThrowExceptionWhenGenderIsMaleButPeselIndicatesFemale() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate(VALID_PESEL_FEMALE, VALID_DOB_FEMALE, UserGender.MALE)
            );
            assertThat(exception.getMessage()).isEqualTo("Gender does not match PESEL");
        }

        @Test
        void validate_shouldThrowExceptionWhenGenderIsFemaleButPeselIndicatesMale() {
            ValidationException exception = assertThrows(ValidationException.class, () ->
                    peselValidator.validate(VALID_PESEL_MALE, VALID_DOB_MALE, UserGender.FEMALE)
            );
            assertThat(exception.getMessage()).isEqualTo("Gender does not match PESEL");
        }
    }
}