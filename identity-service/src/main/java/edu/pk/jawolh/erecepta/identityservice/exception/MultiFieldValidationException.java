package edu.pk.jawolh.erecepta.identityservice.exception;

import jakarta.validation.ValidationException;
import lombok.Data;
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
