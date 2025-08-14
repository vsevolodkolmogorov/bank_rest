package com.example.bankcards.exception;

public class AuthenticationProcessException extends RuntimeException {
    public AuthenticationProcessException(String text) {
        super(text);
    }
}
