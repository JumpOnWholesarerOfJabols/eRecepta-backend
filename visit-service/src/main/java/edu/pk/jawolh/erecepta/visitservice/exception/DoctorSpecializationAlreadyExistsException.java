package edu.pk.jawolh.erecepta.visitservice.exception;

public class DoctorSpecializationAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "DoctorSpecialization already exists";

    public DoctorSpecializationAlreadyExistsException() {
        super(MESSAGE);
    }
}
