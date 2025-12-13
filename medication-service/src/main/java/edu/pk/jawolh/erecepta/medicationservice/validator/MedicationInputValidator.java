package edu.pk.jawolh.erecepta.medicationservice.validator;

import com.example.demo.codegen.types.CreateMedicationInput;
import com.example.demo.codegen.types.IngredientInput;
import com.example.demo.codegen.types.PatchMedicationInput;
import com.example.demo.codegen.types.UpdateIngredientInput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class MedicationInputValidator {

    private static final Pattern EAN_PATTERN = Pattern.compile("^[0-9]{8}$|^[0-9]{12}$|^[0-9]{13}$|^[0-9]{14}$");

    private static final Pattern ATC_PATTERN = Pattern.compile("^[A-Z][0-9]{2}[A-Z]{2}[0-9]{0,2}$");

    private static final Pattern STRENGTH_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?\\s*[a-zA-Z%]+$");


    public void validateCreationInput(CreateMedicationInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Medication input data cannot be null");
        }

        validateEan(input.getEan());
        validateAtcCode(input.getAtcCode());
        validateRequiredString(input.getTradeName(), "Trade Name");
        validateRequiredString(input.getGenericName(), "Generic Name");
        validateRequiredString(input.getManufacturer(), "Manufacturer");
        validateRequiredObject(input.getForm(), "Medication Form");
        validateRequiredObject(input.getRoute(), "Route of Administration");
        validatePackageSize(input.getPackageSize());
        validateRequiredObject(input.getRequiresPrescription(), "Requires Prescription Flag");
        validateIngredients(input.getIngredients());
        validateStringList(input.getIndications(), "Indications");
        validateStringList(input.getSideEffects(), "Side Effects");
    }

    public void validatePatchInput(PatchMedicationInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Patch input data cannot be null");
        }

        if (input.getEan() != null) {
            validateEan(input.getEan());
        }

        if (input.getAtcCode() != null) {
            validateAtcCode(input.getAtcCode());
        }

        if (input.getTradeName() != null) {
            validateRequiredString(input.getTradeName(), "Trade Name");
        }

        if (input.getGenericName() != null) {
            validateRequiredString(input.getGenericName(), "Generic Name");
        }

        if (input.getManufacturer() != null) {
            validateRequiredString(input.getManufacturer(), "Manufacturer");
        }

        if (input.getPackageSize() != null) {
            validatePackageSize(input.getPackageSize());
        }
    }

    public void validateEan(String ean) {
        if (ean == null || ean.trim().isEmpty()) {
            throw new IllegalArgumentException("EAN code is mandatory");
        }
        if (!EAN_PATTERN.matcher(ean).matches()) {
            throw new IllegalArgumentException("Invalid EAN code format or length");
        }
    }

    public void validateAtcCode(String atcCode) {
        if (atcCode == null || atcCode.trim().isEmpty()) {
            throw new IllegalArgumentException("ATC code is mandatory");
        }

        if (!ATC_PATTERN.matcher(atcCode.toUpperCase()).matches()) {
            throw new IllegalArgumentException("Invalid ATC code format (expected format like A01AA or A01AA01)");
        }
    }

    public void validatePackageSize(String packageSize) {
        if (packageSize == null || packageSize.trim().isEmpty()) {
            throw new IllegalArgumentException("Package size is mandatory");
        }
    }

    private void validateIngredients(List<IngredientInput> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Medication must contain at least one ingredient");
        }

        for (int i = 0; i < ingredients.size(); i++) {
            IngredientInput ingredient = ingredients.get(i);
            validateIngredientInput(ingredient);
        }
    }

    public void validateIngredientInput(IngredientInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }

        validateRequiredString(input.getName(), "Ingredient Name");

        if (input.getStrength() == null || !STRENGTH_PATTERN.matcher(input.getStrength()).matches()) {
            throw new IllegalArgumentException(
                    "Invalid strength format. Expected format like '500mg', '10 ml', '2.5 g'"
            );
        }
    }

    public void validateUpdateIngredientInput(UpdateIngredientInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Update input cannot be null");
        }

        if (input.getName() != null) {
            validateRequiredString(input.getName(), "Ingredient Name");
        }

        if (input.getStrength() != null) {
            if (!STRENGTH_PATTERN.matcher(input.getStrength()).matches()) {
                throw new IllegalArgumentException(
                        "Invalid strength format. Expected format like '500mg', '10 ml', '2.5 g'"
                );
            }
        }
    }

    public void validateRequiredString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", fieldName));
        }
    }

    public void validateRequiredObject(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(String.format("%s is mandatory", fieldName));
        }
    }

    private void validateStringList(List<String> list, String fieldName) {
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            if (item == null || item.trim().isEmpty()) {
                throw new IllegalArgumentException(String.format("%s element at index %d cannot be null or empty", fieldName, i));
            }
        }
    }
}