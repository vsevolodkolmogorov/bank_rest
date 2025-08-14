package com.example.bankcards.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String text) {
        super(text);
    }
}
