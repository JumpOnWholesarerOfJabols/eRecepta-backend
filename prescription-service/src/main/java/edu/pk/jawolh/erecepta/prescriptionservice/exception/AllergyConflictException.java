package edu.pk.jawolh.erecepta.prescriptionservice.exception;

import java.util.List;

public class AllergyConflictException extends RuntimeException {
    private final List<String> conflictingIngredients;

    public AllergyConflictException(String message, List<String> conflictingIngredients) {
        super(message);
        this.conflictingIngredients = conflictingIngredients;
    }

    public List<String> getConflictingIngredients() {
        return conflictingIngredients;
    }
}
