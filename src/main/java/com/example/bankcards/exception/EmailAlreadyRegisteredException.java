package com.example.bankcards.exception;

public class EmailAlreadyRegisteredException extends RuntimeException{
    public EmailAlreadyRegisteredException(String text) {
        super(text);
    }
}
