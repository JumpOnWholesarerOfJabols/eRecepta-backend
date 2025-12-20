package edu.pk.jawolh.erecepta.prescriptionservice.exception;

public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(String message) {
        super(message);
    }
}
