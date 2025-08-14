package com.example.bankcards.exception;


public class StatusCodeNotFoundException extends RuntimeException{
    public StatusCodeNotFoundException(String text) {
        super(text);
    }
}
