package edu.pk.jawolh.erecepta.med_docs_service.exceptions;

public class PrescriptionCancelledException extends RuntimeException {
    public PrescriptionCancelledException(String message) {
        super(message);
    }
}
