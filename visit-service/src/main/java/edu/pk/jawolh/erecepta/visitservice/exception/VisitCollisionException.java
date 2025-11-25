package edu.pk.jawolh.erecepta.visitservice.exception;

public class VisitCollisionException extends AbstractNotFoundException {
    private static final String MESSAGE = "visitTime collides with another existing visit";

    public VisitCollisionException() {
        super(MESSAGE);
    }
}
