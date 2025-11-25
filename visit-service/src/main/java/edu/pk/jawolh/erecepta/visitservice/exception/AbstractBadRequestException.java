package edu.pk.jawolh.erecepta.visitservice.exception;

public abstract class AbstractBadRequestException extends RuntimeException {
    protected AbstractBadRequestException(String message) {
        super(message);
    }
}
