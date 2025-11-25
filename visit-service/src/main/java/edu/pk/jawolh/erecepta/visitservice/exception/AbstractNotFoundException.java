package edu.pk.jawolh.erecepta.visitservice.exception;

public abstract class AbstractNotFoundException extends RuntimeException {
    protected AbstractNotFoundException(String message) {
        super(message);
    }
}
