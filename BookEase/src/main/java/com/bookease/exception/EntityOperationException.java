package com.bookease.exception;

public class EntityOperationException extends RuntimeException {
    public EntityOperationException(String message) {
        super(message);
    }

    public EntityOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
