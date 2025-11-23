package edu.pk.jawolh.erecepta.visitservice.exception;

public class WeeklyAvailabilityNotFoundException extends RuntimeException {
    private static final String MESSAGE = "WeeklyAvailability not found";

    public WeeklyAvailabilityNotFoundException() {
        super(MESSAGE);
    }
}
