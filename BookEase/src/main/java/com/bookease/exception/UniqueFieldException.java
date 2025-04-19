package com.bookease.exception;

public class UniqueFieldException extends RuntimeException {
    public UniqueFieldException(String message) {
        super(message);
    }
    public UniqueFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
