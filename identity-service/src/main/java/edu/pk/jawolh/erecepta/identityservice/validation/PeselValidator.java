package edu.pk.jawolh.erecepta.identityservice.validation;

import edu.pk.jawolh.erecepta.common.user.enums.UserGender;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PeselValidator {

    public void validate(String pesel, LocalDate dateOfBirth, UserGender gender){
        if (pesel == null || !pesel.matches("\\d{11}")) {
            throw new ValidationException("PESEL must contain exactly 11 digits");
        }

        if (!isValidChecksum(pesel)) {
            throw new ValidationException("Invalid PESEL checksum");
        }

        LocalDate dateFromPesel;
        try {
            dateFromPesel = extractDateFromPesel(pesel);
        } catch (Exception e) {
            throw new ValidationException("PESEL contains invalid date of birth");
        }

        if (!dateOfBirth.equals(dateFromPesel)) {
            throw new ValidationException("Date of birth does not match PESEL");
        }

        if (gender != null && !isGenderConsistent(pesel, gender)) {
            throw new ValidationException("Gender does not match PESEL");
        }



    }

    private boolean isValidChecksum(String pesel) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(pesel.charAt(i)) * weights[i];
        }
        int checksum = (10 - (sum % 10)) % 10;
        return checksum == Character.getNumericValue(pesel.charAt(10));
    }

    private LocalDate extractDateFromPesel(String pesel) {
        int year = Integer.parseInt(pesel.substring(0, 2));
        int month = Integer.parseInt(pesel.substring(2, 4));
        int day = Integer.parseInt(pesel.substring(4, 6));

        int century;
        if (month >= 1 && month <= 12) {
            century = 1900;
        } else if (month >= 21 && month <= 32) {
            century = 2000;
            month -= 20;
        } else if (month >= 41 && month <= 52) {
            century = 2100;
            month -= 40;
        } else if (month >= 61 && month <= 72) {
            century = 2200;
            month -= 60;
        } else if (month >= 81 && month <= 92) {
            century = 1800;
            month -= 80;
        } else {
            throw new IllegalArgumentException("Invalid month in PESEL");
        }

        return LocalDate.of(century + year, month, day);
    }

    private boolean isGenderConsistent(String pesel, UserGender gender) {
        int genderDigit = Character.getNumericValue(pesel.charAt(9));
        boolean isMaleFromPesel = genderDigit % 2 == 1;

        return switch (gender) {
            case MALE -> isMaleFromPesel;
            case FEMALE -> !isMaleFromPesel;
        };
    }
}
