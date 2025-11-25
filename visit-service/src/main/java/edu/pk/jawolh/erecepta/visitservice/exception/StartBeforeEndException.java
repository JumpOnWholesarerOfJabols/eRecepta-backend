package edu.pk.jawolh.erecepta.visitservice.exception;

public class StartBeforeEndException extends AbstractBadRequestException {
    private static final String MESSAGE = "startTime before endTime";

    public StartBeforeEndException() {
        super(MESSAGE);
    }
}
