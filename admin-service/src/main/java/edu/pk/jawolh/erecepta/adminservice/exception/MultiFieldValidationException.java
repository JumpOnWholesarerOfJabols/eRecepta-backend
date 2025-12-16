package edu.pk.jawolh.erecepta.adminservice.exception;

import jakarta.validation.ValidationException;
import lombok.Getter;

import java.util.Map;

@Getter
public class MultiFieldValidationException extends ValidationException {
    private final Map<String, String> errors;

    public MultiFieldValidationException(Map<String, String> errors) {
        super("Validation failed for multiple fields");
        this.errors = errors;
    }
}
