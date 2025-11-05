package edu.pk.jawolh.erecepta.identityservice.exception;

public class CodeExpiredException extends RuntimeException {
    public CodeExpiredException(String message) {
        super(message);
    }
}
