package com.example.bankcards.exception;

public class CardBlockRequestNotFoundException extends RuntimeException {
    public CardBlockRequestNotFoundException(String text) {
        super(text);
    }
}
