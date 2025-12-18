package edu.pk.jawolh.erecepta.med_docs_service.exceptions;

public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(String message) {
        super(message);
    }
}
