package com.bookease.exception;

public class PublicKeyLoadingException extends RuntimeException {
    public PublicKeyLoadingException(String message) {
        super(message);
    }

    public PublicKeyLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}