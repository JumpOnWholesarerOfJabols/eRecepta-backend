package edu.pk.jawolh.erecepta.visitservice.exception;

public class AvailabilityExceptionNotFoundException extends RuntimeException {
    private static final String MESSAGE = "AvailabilityException not found";

    public AvailabilityExceptionNotFoundException() {
        super(MESSAGE);
    }
}
