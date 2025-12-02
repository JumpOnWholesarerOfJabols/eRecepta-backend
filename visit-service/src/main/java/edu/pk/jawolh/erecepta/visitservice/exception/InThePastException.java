package edu.pk.jawolh.erecepta.visitservice.exception;

public class InThePastException extends AbstractBadRequestException {
    private static final String MESSAGE = "%s set in the past";

    public InThePastException(String field) {
        super(MESSAGE.formatted(field));
    }
}
