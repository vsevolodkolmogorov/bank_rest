package com.example.bankcards.exception;

public class UserRoleSameException extends RuntimeException {
    public UserRoleSameException(String text) {
        super(text);
    }
}
