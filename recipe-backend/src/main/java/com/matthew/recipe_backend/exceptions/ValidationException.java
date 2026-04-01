package com.matthew.recipe_backend.exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException() {
        super("Validation error");
    }

    public ValidationException(String message) {
        super(message);
    }
}
