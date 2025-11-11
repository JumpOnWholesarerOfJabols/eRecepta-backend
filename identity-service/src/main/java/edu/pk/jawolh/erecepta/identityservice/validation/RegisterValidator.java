package edu.pk.jawolh.erecepta.identityservice.validation;

import edu.pk.jawolh.erecepta.identityservice.exception.MultiFieldValidationException;
import edu.pk.jawolh.erecepta.identityservice.model.UserGender;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> errors = new HashMap<>();

        try {
            peselValidator.validate(pesel, dateOfBirth, gender);
        } catch (ValidationException ex) {
            errors.put("pesel", ex.getMessage());
        }

        if (isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.put("email", "Invalid email address format");
        }

        if (isBlank(firstName) || !NAME_PATTERN.matcher(firstName.trim()).matches()) {
            errors.put("firstName", "Invalid first name");
        }

        if (isBlank(lastName) || !NAME_PATTERN.matcher(lastName.trim()).matches()) {
            errors.put("lastName", "Invalid last name");
        }

        if (!isBlank(phoneNumber) && !PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            errors.put("phoneNumber", "Invalid phone number format");
        }

        validatePassword(password, errors);

        if (!errors.isEmpty()) {
            throw new MultiFieldValidationException(errors);
        }
    }

    private void validatePassword(String password, Map<String, String> errors) {
        if (isBlank(password)) {
            errors.put("password", "Password cannot be empty");
            return;
        }

        String trimmed = password.trim();
        if (trimmed.length() < 8) {
            errors.put("password", "Password must be at least 8 characters long");
        }
        if (!trimmed.matches(".*[A-Z].*")) {
            errors.put("password", "Password must contain at least one uppercase letter");
        }
        if (!trimmed.matches(".*[a-z].*")) {
            errors.put("password", "Password must contain at least one lowercase letter");
        }
        if (!trimmed.matches(".*\\d.*")) {
            errors.put("password", "Password must contain at least one number");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
