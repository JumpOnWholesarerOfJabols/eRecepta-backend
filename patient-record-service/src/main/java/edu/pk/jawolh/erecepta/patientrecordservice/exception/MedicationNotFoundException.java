package edu.pk.jawolh.erecepta.patientrecordservice.exception;

public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(String message) {
        super(message);
    }
}
