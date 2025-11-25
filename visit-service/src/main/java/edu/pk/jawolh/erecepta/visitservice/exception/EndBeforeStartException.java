package edu.pk.jawolh.erecepta.visitservice.exception;

public class EndBeforeStartException extends AbstractBadRequestException {
    private static final String MESSAGE = "endTime before startTime";

    public EndBeforeStartException() {
        super(MESSAGE);
    }
}
