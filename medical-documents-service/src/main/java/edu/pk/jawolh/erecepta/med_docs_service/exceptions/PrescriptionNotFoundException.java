package edu.pk.jawolh.erecepta.med_docs_service.exceptions;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(String message) {
        super(message);
    }
}
