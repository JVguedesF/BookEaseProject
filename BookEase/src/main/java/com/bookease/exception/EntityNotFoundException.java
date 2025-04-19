package com.bookease.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object identifier) {
        super(entityName + " with identifier " + identifier + " not found");
    }
}
