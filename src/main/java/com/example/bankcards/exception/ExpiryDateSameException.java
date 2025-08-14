package com.example.bankcards.exception;

public class ExpiryDateSameException extends RuntimeException{
    public ExpiryDateSameException(String text) {
        super(text);
    }
}
