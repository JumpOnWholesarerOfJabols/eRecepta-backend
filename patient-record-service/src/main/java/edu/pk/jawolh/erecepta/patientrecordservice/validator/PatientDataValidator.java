package edu.pk.jawolh.erecepta.patientrecordservice.validator;

import com.example.demo.codegen.types.UpdatePatientInfoInput;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PatientDataValidator {

    private static final double MIN_VAL = 0.0;
    private static final double MAX_HEIGHT_CM = 250.0;
    private static final double MAX_WEIGHT_KG = 600.0;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-]{9,15}$");

    public void validateUpdateInput(UpdatePatientInfoInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }

        validateHeight(input.getHeight());
        validateWeight(input.getWeight());
        validateEmergencyContact(input.getEmergencyContact());
    }

    public void validateAllergyName(String allergy) {
        if (allergy == null || allergy.trim().isEmpty()) {
            throw new IllegalArgumentException("Allergy name cannot be empty");
        }
    }

    public void validateDiseaseName(String disease) {
        if (disease == null || disease.trim().isEmpty()) {
            throw new IllegalArgumentException("Disease name cannot be empty");
        }
    }


    private void validateHeight(Double height) {
        if (height == null)
            return;

        if (height < MIN_VAL || height > MAX_HEIGHT_CM) {
            throw new IllegalArgumentException(
                    String.format("Height must be between %.0f and %.0f cm", MIN_VAL, MAX_HEIGHT_CM));
        }
    }

    private void validateWeight(Double weight) {
        if (weight == null)
            return;

        if (weight < MIN_VAL || weight > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    String.format("Weight must be between %.0f and %.0f kg", MIN_VAL, MAX_WEIGHT_KG));
        }
    }

    private void validateEmergencyContact(String contact) {
        if (contact == null)
            return;

        if (contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Emergency contact cannot be empty if provided");
        }
        if (!PHONE_PATTERN.matcher(contact).matches()) {
            throw new IllegalArgumentException("Invalid emergency contact format");
        }
    }
}