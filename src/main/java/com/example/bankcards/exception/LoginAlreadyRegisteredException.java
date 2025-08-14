package com.example.bankcards.exception;

public class LoginAlreadyRegisteredException extends RuntimeException{
    public LoginAlreadyRegisteredException(String text) {
        super(text);
    }
}
