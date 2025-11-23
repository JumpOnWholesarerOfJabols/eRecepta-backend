package edu.pk.jawolh.erecepta.visitservice.exception;

public class DoctorSpecializationNotFoundException extends RuntimeException {
    private static final String MESSAGE = "DoctorSpecialization not found";

    public DoctorSpecializationNotFoundException() {
        super(MESSAGE);
    }
}
