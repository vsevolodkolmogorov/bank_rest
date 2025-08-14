package com.example.bankcards.exception;

public class StatusCodeSameException extends RuntimeException {
    public StatusCodeSameException(String text) {
        super(text);
    }
}
