package com.example.bankcards.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String text) {
        super(text);
    }
}
