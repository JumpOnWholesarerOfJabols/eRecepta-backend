package edu.pk.jawolh.erecepta.med_docs_service.exceptions;

public class PrescriptionOverfulfillmentException extends RuntimeException {
    public PrescriptionOverfulfillmentException(String message) {
        super(message);
    }
}
