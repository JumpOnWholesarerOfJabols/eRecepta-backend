package edu.pk.jawolh.erecepta.patientrecordservice.exception;

public class MedicationAlreadyExistsException extends RuntimeException {
    public MedicationAlreadyExistsException(String message) {
        super(message);
    }
}
