package edu.pk.jawolh.erecepta.visitservice.exception;

public class AvailabilityExceptionCollisionException extends AbstractBadRequestException {
    private static final String MESSAGE = "collision with another AvailabilityException";

    public AvailabilityExceptionCollisionException() {
        super(MESSAGE);
    }
}
