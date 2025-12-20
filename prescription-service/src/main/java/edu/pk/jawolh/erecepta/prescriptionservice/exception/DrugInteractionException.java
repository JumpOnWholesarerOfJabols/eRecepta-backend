package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when two medications have a drug interaction.
 */
public class DrugInteractionException extends RuntimeException {
    /**
     * Constructs a new DrugInteractionException with the specified detail message.
     *
     * @param message the detail message containing both medication IDs
     */
    public DrugInteractionException(String message) {
        super(message);
    }
}
