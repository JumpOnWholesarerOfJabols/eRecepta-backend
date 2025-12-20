package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when a medication with the specified ID does not exist.
 */
public class MedicationNotFoundException extends RuntimeException {
    /**
     * Constructs a new MedicationNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public MedicationNotFoundException(String message) {
        super(message);
    }
}
