package edu.pk.jawolh.erecepta.visitservice.exception;

import java.util.UUID;

public class VisitNotFoundException extends AbstractNotFoundException {
    private static final String MESSAGE = "Visit %s not found";

    public VisitNotFoundException(UUID id) {
        super(MESSAGE.formatted(id));
    }
}
