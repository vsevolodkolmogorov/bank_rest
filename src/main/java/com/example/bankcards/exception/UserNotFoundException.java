package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String text) {
        super(text);
    }
}
