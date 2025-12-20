package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when attempting to create a duplicate prescription for the same visit.
 */
public class DuplicatePrescriptionException extends RuntimeException {
    /**
     * Constructs a new DuplicatePrescriptionException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicatePrescriptionException(String message) {
        super(message);
    }
}
