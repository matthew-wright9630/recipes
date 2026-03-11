package com.matthew.recipe_backend.exceptions;

public class PasswordNotStrongEnoughException extends RuntimeException {
    public PasswordNotStrongEnoughException(String message) {
        super(message);
    }
}
