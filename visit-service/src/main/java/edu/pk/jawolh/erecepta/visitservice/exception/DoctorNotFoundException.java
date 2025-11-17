package edu.pk.jawolh.erecepta.visitservice.exception;

public class DoctorNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Doctor with id %s not found";

    public DoctorNotFoundException(String doctorId) {
        super(MESSAGE.formatted(doctorId));
    }
}
