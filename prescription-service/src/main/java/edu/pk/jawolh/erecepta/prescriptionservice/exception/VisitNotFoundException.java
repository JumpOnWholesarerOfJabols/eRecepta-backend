package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when a visit is not found or does not belong to the specified doctor.
 */
public class VisitNotFoundException extends RuntimeException {
    /**
     * Constructs a new VisitNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public VisitNotFoundException(String message) {
        super(message);
    }
}
