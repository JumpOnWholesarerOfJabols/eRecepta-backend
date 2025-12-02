package edu.pk.jawolh.erecepta.visitservice.exception;

public class OutsideAvailabilityException extends AbstractBadRequestException {
    private static final String MESSAGE = "visitTime outside of doctor's availability window";

    public OutsideAvailabilityException() {
        super(MESSAGE);
    }
}
