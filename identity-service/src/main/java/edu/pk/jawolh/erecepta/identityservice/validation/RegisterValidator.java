package edu.pk.jawolh.erecepta.identityservice.validation;

import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RegisterValidator {

    private final PeselValidator peselValidator;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{9,15}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-ZĄĆĘŁŃÓŚŹŻ][a-ząćęłńóśźżA-ZĄĆĘŁŃÓŚŹŻ '-]{1,49}$");

    public void validateRegistrationData(
            String pesel,
            LocalDate dateOfBirth,
            UserGender gender,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String password
    ) {

        peselValidator.validate(pesel, dateOfBirth, gender);

        if (isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email address format");
        }

        if (isBlank(firstName) || !NAME_PATTERN.matcher(firstName.trim()).matches()) {
            throw new ValidationException("Invalid first name");
        }

        if (isBlank(lastName) || !NAME_PATTERN.matcher(lastName.trim()).matches()) {
            throw new ValidationException("Invalid last name");
        }

        if (!isBlank(phoneNumber) && !PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            throw new ValidationException("Invalid phone number format");
        }

        validatePassword(password);
    }

    private void validatePassword(String password) {
        if (isBlank(password)) {
            throw new ValidationException("Password cannot be empty");
        }

        String trimmed = password.trim();
        if (trimmed.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }

        if (!trimmed.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!trimmed.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!trimmed.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
