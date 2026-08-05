package com.matthew.recipe_backend.exceptions;

public class CookbookExistsException extends RuntimeException {

    public CookbookExistsException(String message) {
        super(message);
    }
}
