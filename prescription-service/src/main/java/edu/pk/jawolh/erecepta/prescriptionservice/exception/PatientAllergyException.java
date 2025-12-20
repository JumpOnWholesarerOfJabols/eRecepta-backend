package edu.pk.jawolh.erecepta.prescriptionservice.exception;

/**
 * Exception thrown when a patient is allergic to an ingredient in a prescribed medication.
 */
public class PatientAllergyException extends RuntimeException {
    /**
     * Constructs a new PatientAllergyException with the specified detail message.
     *
     * @param message the detail message containing ingredient name and medication ID
     */
    public PatientAllergyException(String message) {
        super(message);
    }
}
