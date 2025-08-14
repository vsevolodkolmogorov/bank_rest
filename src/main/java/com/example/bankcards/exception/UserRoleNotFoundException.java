package com.example.bankcards.exception;

public class UserRoleNotFoundException extends RuntimeException{
    public UserRoleNotFoundException(String text) {
        super(text);
    }
}
