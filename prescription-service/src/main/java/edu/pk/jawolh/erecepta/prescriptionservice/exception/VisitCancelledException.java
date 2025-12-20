package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when attempting to create a prescription for a cancelled visit.
 */
public class VisitCancelledException extends RuntimeException {
    /**
     * Constructs a new VisitCancelledException with the specified detail message.
     *
     * @param message the detail message
     */
    public VisitCancelledException(String message) {
        super(message);
    }
}
