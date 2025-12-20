package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when attempting to create a prescription for a visit that hasn't occurred yet.
 */
public class FutureVisitException extends RuntimeException {
    /**
     * Constructs a new FutureVisitException with the specified detail message.
     *
     * @param message the detail message
     */
    public FutureVisitException(String message) {
        super(message);
    }
}
