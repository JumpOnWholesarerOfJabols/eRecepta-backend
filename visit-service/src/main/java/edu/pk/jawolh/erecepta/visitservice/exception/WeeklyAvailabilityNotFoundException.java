package edu.pk.jawolh.erecepta.visitservice.exception;

import java.time.DayOfWeek;
import java.util.UUID;

public class WeeklyAvailabilityNotFoundException extends AbstractBadRequestException {
    private static final String MESSAGE = "Doctor %s is not available on %s";

    public WeeklyAvailabilityNotFoundException(UUID doctorId, DayOfWeek dayOfWeek) {
        super(MESSAGE.formatted(doctorId, dayOfWeek));
    }
}
