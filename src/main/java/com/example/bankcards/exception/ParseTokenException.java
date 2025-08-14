package com.example.bankcards.exception;

public class ParseTokenException extends RuntimeException {
    public ParseTokenException(String text) {
        super(text);
    }
}
