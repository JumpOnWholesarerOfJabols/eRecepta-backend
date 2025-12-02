package edu.pk.jawolh.erecepta.visitservice.exception;

import java.util.UUID;

public class DoctorNotFoundException extends AbstractNotFoundException {
    private static final String MESSAGE = "Doctor with id %s not found";

    public DoctorNotFoundException(UUID doctorId) {
        super(MESSAGE.formatted(doctorId));
    }
}
