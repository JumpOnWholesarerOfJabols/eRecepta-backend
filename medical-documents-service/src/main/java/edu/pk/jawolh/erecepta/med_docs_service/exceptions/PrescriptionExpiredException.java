package edu.pk.jawolh.erecepta.med_docs_service.exceptions;

public class PrescriptionExpiredException extends RuntimeException {
    public PrescriptionExpiredException(String message) {
        super(message);
    }
}
