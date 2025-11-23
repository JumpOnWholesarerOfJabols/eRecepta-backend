package edu.pk.jawolh.erecepta.visitservice.exception;

public class VisitNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Visit with id %s not found";

    public VisitNotFoundException(String visitId) {
        super(MESSAGE.formatted(visitId));
    }
}
