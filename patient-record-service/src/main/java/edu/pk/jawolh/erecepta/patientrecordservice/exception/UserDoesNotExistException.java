package edu.pk.jawolh.erecepta.patientrecordservice.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
