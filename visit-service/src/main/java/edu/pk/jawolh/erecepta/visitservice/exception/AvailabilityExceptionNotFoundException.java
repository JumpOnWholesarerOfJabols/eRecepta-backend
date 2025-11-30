package edu.pk.jawolh.erecepta.visitservice.exception;

import java.util.UUID;

public class AvailabilityExceptionNotFoundException extends AbstractNotFoundException {
    private static final String MESSAGE = "AvailabilityException %s not found";

    public AvailabilityExceptionNotFoundException(UUID id) {
        super(MESSAGE.formatted(id));
    }
}
